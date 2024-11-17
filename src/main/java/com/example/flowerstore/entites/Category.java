package com.example.flowerstore.entites;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
public class Category {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long categoryId;

        @NotBlank
        private String categoryName;

        @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,orphanRemoval = true)
        private List<Product> products;
}
