package com.aninenha.todolist.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {
//    ID
//    Usuário (ID_USUARIO)
//    Descrição
//    Título
//    Início
//    Término
//    Prioridade
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String description;

    @Column(length = 70)
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime finishAt;
    private String priority;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private UUID idUser;

    public void setTitle(String title) throws Exception {
        if (title.length()>70){
            throw new Exception("Título pode conter até 70 caracteres.");
        }
        this.title = title;
    }
}
