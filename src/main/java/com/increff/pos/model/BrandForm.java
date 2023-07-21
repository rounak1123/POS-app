package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class BrandForm {
	private String brand;

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
