package com.rcp.commerce.catalog.application.port.in;

import com.rcp.commerce.catalog.domain.Category;
import com.rcp.commerce.catalog.domain.CategoryId;
import java.util.List;

public interface CategoryAdminUseCase {

    Category createCategory(CategoryCommand command);

    Category updateCategory(CategoryId categoryId, CategoryCommand command);

    List<Category> listCategories();

    Category getCategory(CategoryId categoryId);
}
