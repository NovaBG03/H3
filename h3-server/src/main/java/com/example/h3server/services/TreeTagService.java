package com.example.h3server.services;

import com.example.h3server.models.TreeTag;
import com.example.h3server.repositories.TreeTagRepository;
import org.springframework.stereotype.Service;

@Service
public class TreeTagService {

    private final TreeTagRepository treeTagRepository;

    public TreeTagService(TreeTagRepository treeTagRepository) {
        this.treeTagRepository = treeTagRepository;
    }

    public TreeTag getOrCreateTreeTag(String label) {
        TreeTag tag = treeTagRepository.findByLabel(label);

        if (tag != null) {
            return tag;
        }

        return treeTagRepository.save(TreeTag.builder().label(label).build());
    }
}
