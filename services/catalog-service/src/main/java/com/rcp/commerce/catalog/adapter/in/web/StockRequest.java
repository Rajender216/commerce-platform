package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.StockCommand;
import jakarta.validation.constraints.PositiveOrZero;

public record StockRequest(@PositiveOrZero int stockQuantity) {

    StockCommand toCommand() {
        return new StockCommand(stockQuantity);
    }
}
