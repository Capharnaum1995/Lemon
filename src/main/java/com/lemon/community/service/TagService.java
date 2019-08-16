package com.lemon.community.service;

import com.lemon.community.dto.TagDTO;
import com.lemon.community.mapper.TagMapper;
import com.lemon.community.model.Tag;
import com.lemon.community.model.TagExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    TagMapper tagMapper;

    /**
     * 获取标签库的所有标签
     *
     * @return
     */
    public TagDTO getTagLibrary() {
        List<Tag> tagList = tagMapper.selectByExample(new TagExample());
        TagDTO tagDTO = new TagDTO();
        tagDTO.setTagList(tagList);
        return tagDTO;
    }
}
