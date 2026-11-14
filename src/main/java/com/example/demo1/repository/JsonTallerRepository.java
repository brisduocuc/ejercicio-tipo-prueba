package com.example.demo1.repository;

import com.example.demo1.model.Taller;
import com.example.demo1.model.TallerPresencial;
import com.example.demo1.model.TallerVirtual;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonTallerRepository implements TallerRepository {

    private final File archivoPresenciales = new File("talleres_presenciales.json");
    private final File archivoVirtuales = new File("talleres_virtuales.json");
    private final ObjectMapper mapper;

    public JsonTallerRepository() {
        this.mapper = new ObjectMapper();
        // Formatea el JSON con indentación legible
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public List<Taller> cargarTodos() throws IOException {
        List<Taller> resultado = new ArrayList<>();

        if (archivoPresenciales.exists() && archivoPresenciales.length() > 0) {
            List<TallerPresencial> presenciales = mapper.readValue(
                    archivoPresenciales,
                    new TypeReference<List<TallerPresencial>>() {}
            );
            resultado.addAll(presenciales);
        }

        if (archivoVirtuales.exists() && archivoVirtuales.length() > 0) {
            List<TallerVirtual> virtuales = mapper.readValue(
                    archivoVirtuales,
                    new TypeReference<List<TallerVirtual>>() {}
            );
            resultado.addAll(virtuales);
        }

        return resultado;
    }

    @Override
    public void guardarTodos(List<Taller> talleres) throws IOException {
        List<TallerPresencial> presenciales = new ArrayList<>();
        List<TallerVirtual> virtuales = new ArrayList<>();

        for (Taller t : talleres) {
            if (t instanceof TallerPresencial tp) {
                presenciales.add(tp);
            } else if (t instanceof TallerVirtual tv) {
                virtuales.add(tv);
            }
        }

        mapper.writeValue(archivoPresenciales, presenciales);
        mapper.writeValue(archivoVirtuales, virtuales);
    }
}