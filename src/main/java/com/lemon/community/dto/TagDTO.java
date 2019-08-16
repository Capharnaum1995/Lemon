package com.lemon.community.dto;

import com.lemon.community.model.Tag;
import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    List<Tag> tagList;
}
