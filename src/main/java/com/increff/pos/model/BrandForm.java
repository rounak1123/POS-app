package com.increff.pos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class BrandForm {
	@NotBlank(message = "Brand cannot be empty")
	@Pattern(regexp = "[^-a-zA-Z0-9_*#@!.&%\\s]", message = "Brand contains invalid character.")
	@Size(max=30, message = "Brand length must be less than 30 characters")
	private String brand;

	@NotBlank(message = "Category cannot be empty")
	@Pattern(regexp = "[^-a-zA-Z0-9_*#@!.&%\\s]", message = "Category contains invalid character.")
	@Size(max=30, message = "Category length must be less than 30 characters")
	private String category;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		BrandForm other = (BrandForm) obj;
		return Objects.equals(category,other.category) && Objects.equals(brand, other.brand);
	}

	@Override
	public int hashCode() {
		return Objects.hash(brand, category);
	}
}
