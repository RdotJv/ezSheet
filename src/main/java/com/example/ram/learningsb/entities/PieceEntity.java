package com.example.ram.learningsb.entities;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "sheet_data")
public class PieceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sheetid;

    private String composername;
    private String sheetname;
    private String sheeturl;
    private String filename;
    private ArrayList<String> unscrapedvariations;
    private ArrayList<String> scrapedvariations;
}
