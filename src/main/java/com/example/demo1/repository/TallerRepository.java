package com.example.demo1.repository;

import com.example.demo1.model.Taller;
import java.io.IOException;
import java.util.List;

public interface TallerRepository {
    List<Taller> cargarTodos() throws IOException;
    void guardarTodos(List<Taller> talleres) throws IOException;
}