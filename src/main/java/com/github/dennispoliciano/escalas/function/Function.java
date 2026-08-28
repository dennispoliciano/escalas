package com.github.dennispoliciano.escalas.function;

import com.github.dennispoliciano.escalas.group.Group;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "functions")
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String icon;

    private Boolean active;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    protected Function() {
    }

    public Function(String name, String icon, Group group) {
        this.name = name;
        this.icon = icon;
        this.active = true;
        setGroup(group);
    }

    public Long getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("O campo 'group' não pode ser nulo.");
        }
        this.group = group;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
