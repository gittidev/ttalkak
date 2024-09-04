package com.ttalkak.project.project.domain.model;

import com.ttalkak.project.project.domain.model.vo.ProjectStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private Long userId;

    private String projectName;

    private String domainName;

    @Column(name = "status")
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @Builder
    private ProjectEntity(Long id, Long userId, String projectName, String domainName, ProjectStatus status) {
        this.id = id;
        this.userId = userId;
        this.projectName = projectName;
        this.domainName = domainName;
    }

    public ProjectEditor.ProjectEditorBuilder toEditor() {
        return ProjectEditor.builder()
                .projectName(this.domainName)
                .domainName(this.domainName);

    }

    public void edit(ProjectEditor projectEditor) {
        this.projectName = projectEditor.projectName;
        this.domainName = projectEditor.domainName;
    }

    public void rollbackDeletedStatus() {
        this.status = ProjectStatus.ACTIVE;
    }

    public void updateDeletedStatus() {
        this.status = ProjectStatus.DELETED;
    }
}
