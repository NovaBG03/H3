package com.example.h3server.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FamilyTree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "FamilyTree name must be from 3 to 225 symbols")
    @Size(min = 3, max = 225, message = "FamilyTree name must be from 3 to 225 symbols")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Something went wrong")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Something went wrong")
    @Column(nullable = false)
    private Boolean isPrivate;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "familyTree", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<FamilyMember> familyMembers = new HashSet<>();

    @ManyToMany()
    @JoinTable(
            name = "trees_tags",
            joinColumns = @JoinColumn(name = "family_tree_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TreeTag> tags = new HashSet<>();

    @Builder
    public FamilyTree(Long id,
                      String name,
                      LocalDateTime createdAt,
                      Boolean isPrivate,
                      User user,
                      Set<FamilyMember> familyMembers,
                      Set<TreeTag> tags) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.isPrivate = isPrivate;
        this.user = user;
        if (familyMembers != null) {
            this.familyMembers = familyMembers;
        }
        if (tags != null) {
            this.tags = tags;
        }
    }

    public void addFamilyMember(FamilyMember familyMember) {
        familyMember.setFamilyTree(this);
        this.familyMembers.add(familyMember);
    }

    public FamilyMember getFamilyMember(Long id) {
        return this.familyMembers
                .stream()
                .filter(member -> member.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addTag(TreeTag treeTag) {
        treeTag.getFamilyTrees().add(this);
        this.tags.add(treeTag);
    }
}
