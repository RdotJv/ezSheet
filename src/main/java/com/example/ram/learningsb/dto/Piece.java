package com.example.ram.learningsb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Piece{
    private int sheetId;
    private String composerName;
    private String sheetName;
    private String sheetUrl;
    private String fileName;


    public Piece(int sheetId, String composerName, String sheetName, String sheetUrl, String fileName) {
        this.sheetId = sheetId;
        this.composerName = composerName;
        this.sheetName = sheetName;
        this.sheetUrl = sheetUrl;
        this.fileName = fileName;
    }
}

