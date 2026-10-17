param(
    [string] $GatewayUrl = "http://localhost:8080",
    [string] $KeycloakUrl = "http://localhost:8081",
    [string] $EurekaUrl = "http://localhost:8761"
)

$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Net.Http

function Get-Token {
    param(
        [string] $Username,
        [string] $Password
    )

    return (Invoke-RestMethod `
            -Method Post `
            -Uri "$KeycloakUrl/realms/commerce-mvp/protocol/openid-connect/token" `
            -ContentType "application/x-www-form-urlencoded" `
            -Body @{
                grant_type = "password"
                client_id = "commerce-web"
                username = $Username
                password = $Password
            }).access_token
}

function Invoke-SmokeRequest {
    param(
        [string] $Method,
        [string] $Uri,
        [hashtable] $Headers = @{}
    )

    $client = [System.Net.Http.HttpClient]::new()
    $request = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new($Method), $Uri)
    foreach ($header in $Headers.GetEnumerator()) {
        [void] $request.Headers.TryAddWithoutValidation($header.Key, $header.Value)
    }
    try {
        return $client.SendAsync($request).GetAwaiter().GetResult()
    } finally {
        $request.Dispose()
        $client.Dispose()
    }
}

function Assert-Status {
    param(
        [System.Net.Http.HttpResponseMessage] $Response,
        [int] $Status
    )

    if ([int] $Response.StatusCode -ne $Status) {
        $body = $Response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        if ($body.Length -gt 500) {
            $body = $body.Substring(0, 500)
        }
        throw "Expected HTTP $Status but got $([int] $Response.StatusCode). Body: $body"
    }
}

function Get-RegisteredApps {
    try {
        $apps = Invoke-RestMethod -Uri "$EurekaUrl/eureka/apps" -Headers @{ Accept = "application/json" }
        $applications = @($apps.applications.application)
        if ($applications.Count -eq 0) {
            return @()
        }
        return @($applications | ForEach-Object { $_.name })
    } catch {
        return @()
    }
}

Write-Host "Checking Eureka registry..."
$expectedApps = @("API-GATEWAY", "CATALOG-SERVICE", "CUSTOMER-SERVICE")
$registered = @()
for ($attempt = 1; $attempt -le 24; $attempt++) {
    $registered = Get-RegisteredApps
    $missing = @($expectedApps | Where-Object { $registered -notcontains $_ })
    if ($missing.Count -eq 0) {
        break
    }
    Start-Sleep -Seconds 5
}
foreach ($expected in $expectedApps) {
    if ($registered -notcontains $expected) {
        throw "Expected $expected in Eureka registry. Found: $($registered -join ', ')"
    }
}

Write-Host "Calling public Catalog API through Gateway..."
$publicHeaders = @{ "X-Correlation-ID" = "smoke-catalog-public" }
$publicResponse = Invoke-SmokeRequest -Method "GET" -Uri "$GatewayUrl/api/v1/categories" -Headers $publicHeaders
Assert-Status -Response $publicResponse -Status 200
$correlationValues = @($publicResponse.Headers.GetValues("X-Correlation-ID"))
if ($correlationValues.Count -ne 1 -or $correlationValues[0] -ne "smoke-catalog-public") {
    throw "Gateway did not return the expected correlation ID."
}

Write-Host "Obtaining customer token..."
$customerToken = Get-Token -Username "customer@example.test" -Password "customer-local-pass"

Write-Host "Calling Customer API through Gateway..."
$customerResponse = Invoke-SmokeRequest -Method "GET" -Uri "$GatewayUrl/api/v1/customers/me" -Headers @{ Authorization = "Bearer $customerToken" }
Assert-Status -Response $customerResponse -Status 200

Write-Host "Verifying customer token cannot access admin Catalog API..."
$customerAdminResponse = Invoke-SmokeRequest -Method "GET" -Uri "$GatewayUrl/api/v1/admin/categories" -Headers @{ Authorization = "Bearer $customerToken" }
Assert-Status -Response $customerAdminResponse -Status 403

Write-Host "Obtaining admin token..."
$adminToken = Get-Token -Username "admin@example.test" -Password "admin-local-pass"

Write-Host "Calling admin Catalog API through Gateway..."
$adminResponse = Invoke-SmokeRequest -Method "GET" -Uri "$GatewayUrl/api/v1/admin/categories" -Headers @{ Authorization = "Bearer $adminToken" }
Assert-Status -Response $adminResponse -Status 200

Write-Host "Gateway smoke test passed."
