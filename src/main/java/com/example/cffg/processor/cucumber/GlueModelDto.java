package com.example.cffg.processor.cucumber;

import lombok.Data;

import java.lang.annotation.Annotation;

@Data
public class GlueModelDto {
    private Annotation step;
    private boolean isHook = false;
}
