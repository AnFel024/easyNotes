package com.example.easynotes.dto;

import com.example.easynotes.enumerator.TypeNote;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TypeNoteDTO {

    private String title;
    private String author;
    private TypeNote calificacion;

}
