package com.example.h3server.services;

import com.example.h3server.models.TreeTag;
import com.example.h3server.repositories.TreeTagRepository;
import com.example.h3server.utils.ModelValidator;
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

        tag = TreeTag.builder().label(label).build();
        ModelValidator.validate(tag);
        return treeTagRepository.save(tag);
    }

    public void deleteUnnecessaryTreeTag(TreeTag tag) {
        System.out.println(treeTagRepository.findTreesCountWithTag(tag.getId()));
        if (treeTagRepository.findTreesCountWithTag(tag.getId()) == 0) {
            treeTagRepository.delete(tag);
        }
    }
}
