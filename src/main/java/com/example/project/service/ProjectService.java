package com.example.project.service;

import com.example.project.model.AppUser;
import com.example.project.model.Project;
import com.example.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Create a new project
     */
    public Project createProject(String name, String description) {
        Project project = Project.builder()
                .name(name)
                .description(description)
                .build();
        return projectRepository.save(project);
    }

    /**
     * Get project by ID
     */
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    /**
     * Get all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Update project
     */
    public Project updateProject(Long id, String name, String description) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.setName(name);
        project.setDescription(description);
        return projectRepository.save(project);
    }

    /**
     * Delete project
     */
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    /**
     * Get project count
     */
    public long getProjectCount() {
        return projectRepository.count();
    }
}
