package com.example.easynotes.dto;

import com.example.easynotes.enumerator.TypeNote;
import com.example.easynotes.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TypeNoteDTO {

    private String title;
    private String author;
    private TypeNote calificacion;

}
