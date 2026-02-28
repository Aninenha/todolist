package com.aninenha.todolist.task;

import com.aninenha.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public TaskModel create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate= LocalDateTime.now();
        if (currentDate.isBefore(taskModel.getStartAt())) {
            if (taskModel.getStartAt().isBefore(taskModel.getFinishAt())) {
                var task = this.taskRepository.save(taskModel);
                return task;
            } else {
                throw new IllegalArgumentException("A data de início deve ser anterior à data de término.");
            }
        } else {
            throw new IllegalArgumentException("A data de início deve ser no futuro.");
        }

    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = (UUID) request.getAttribute("idUser");
        var list = this.taskRepository.findByIdUser((UUID)idUser);
        return list;
    }

    @PutMapping("/{idTask}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID idTask){

        var task = this.taskRepository.findById(idTask).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        var idUser = request.getAttribute("idUser");

        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar esta tarefa.");
        } else {
            Utils.copyNonNullProperties(taskModel, task);
            var updated = this.taskRepository.save(task);
            return ResponseEntity.ok().body(updated);
        }
    }

}
