package com.lemon.community.service;

import com.lemon.community.dto.PagingDTO;
import com.lemon.community.dto.QuestionDTO;
import com.lemon.community.exception.CustomizeErrorCode;
import com.lemon.community.exception.CustomizeException;
import com.lemon.community.mapper.QuestionMapper;
import com.lemon.community.mapper.QuestionMapperExt;
import com.lemon.community.mapper.TagMapper;
import com.lemon.community.mapper.UserMapper;
import com.lemon.community.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionMapperExt questionMapperExt;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TagMapper tagMapper;

    /**
     * 初始化主页页面信息
     *
     * @param pageSize
     * @param pageNow
     * @return
     */
    public PagingDTO initPage(Integer pageSize, Integer pageNow) {
        Integer pageCount = ((int) questionMapper.countByExample(new QuestionExample()) - 1) / pageSize + 1;

        //增加容错处理：若用户手动输入pageNow的值，导致超出了pageNow的范围，pageNow<1的情况在IndexController里面已经处理
        if (pageNow > pageCount) {
            pageNow = pageCount;
        }
        if (pageNow < 1) {
            pageNow = 1;
        }
        Integer offset = (pageNow - 1) * pageSize;
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(new QuestionExample(), new RowBounds(offset, pageSize));

        //将问题和用户封装在QuestionDTO对象中，再封装成List
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for (Question question : questions) {
            QuestionDTO questionDTO = createQuestionDTO(question);
            questionDTOS.add(questionDTO);
        }
        //将前面封装的List对象，以及分页功能模块封装成PagingDTO对象-->传给IndexController(在页面上解析生成页面)
        PagingDTO<QuestionDTO> pagingDTO = new PagingDTO<>();
        pagingDTO.setData(questionDTOS);
        pagingDTO.initPage(pageCount, pageNow);//初始化页面信息
        return pagingDTO;
    }

    /**
     * 初始化个人我的问题页面信息
     *
     * @param pageSize
     * @param pageNow
     * @param id
     * @return
     */
    public PagingDTO initPage(Integer pageSize, Integer pageNow, Long id) {
        //计算出分页数
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(id);
        Integer pageCount = ((int) questionMapper.countByExample(questionExample) - 1) / pageSize + 1;

        //防止用户在地址栏手动输入
        if (pageNow > pageCount) {
            pageNow = pageCount;
        }
        if (pageNow < 1) {
            pageNow = 1;
        }

        //根据pageNow获得所有问题对象
        Integer offset = (pageNow - 1) * pageSize;
        QuestionExample questionExample1 = new QuestionExample();
        questionExample1.createCriteria().andCreatorEqualTo(id);
        List<Question> questions = questionMapper.selectByExampleWithBLOBsWithRowbounds(questionExample1, new RowBounds(offset, pageSize));

        //questions-->questionDTOS
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        User user = userMapper.selectByPrimaryKey(id);
        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        //questionDTOS-->pagingDTO
        PagingDTO<QuestionDTO> pagingDTO = new PagingDTO<>();
        pagingDTO.setData(questionDTOS);
        pagingDTO.initPage(pageCount, pageNow);
        return pagingDTO;
    }

    /**
     * 根据question对象的id得到questionDTO对象
     *
     * @param id
     * @return
     */
    public QuestionDTO getQuestionById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        return createQuestionDTO(question);
    }

    /**
     * @param question
     */
    public void createOrUpdate(Question question) {
        //是新创建的问题，执行插入。这里需要注意的是，这里应该使用insertSelective,插入的时候viewCount等使用的是默认的0，如果用insert的话还得指定插入时
        //该字段的值为零，否则的话不设置会插入null,除了自动生成的主键。
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertSelective(question);
        } else {
            //是编辑已创建的问题，只需用做更新.只跟新某些字段，所以同样使用selective
            Question question1 = new Question();
            question1.setTitle(question.getTitle());
            question1.setDescription(question.getDescription());
            question1.setTag(question.getTag());
            question1.setGmtModified(System.currentTimeMillis());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(question1, questionExample);
            if (updated != 1) {//当更新的时候，另开一个页面将该问题删除，提交的时候会更新失败
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /**
     * 增加阅读数的方法：调用该方法，对应id的问题阅读数加1
     *
     * @param id
     */
    public void increaseViewCount(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1L);
        questionMapperExt.increaseViewCount(question);
    }

    /**
     * 根据question对象得到questionDTO对象
     *
     * @param question
     * @return
     */
    private QuestionDTO createQuestionDTO(Question question) {
        //获取questionDTO的User属性
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        //获取questionDTO的tags属性
        String var1 = question.getTag();                                   //var1="/1/2/3/"
        String var2 = StringUtils.left(var1, var1.length() - 1);      //var2="/1/2/3"
        String var3 = StringUtils.right(var2, var2.length() - 1);     //var3="1/2/3"
        String[] var4 = StringUtils.split(var3, "/");        //var4=["1","2","3"]
        List<Long> var5 = new ArrayList<>();
        for (int i = 0; i < var4.length; i++) {
            var5.add(Long.parseLong(var4[i]));
        }
        TagExample tagExample = new TagExample();
        tagExample.createCriteria().andIdIn(var5);
        List<Tag> tags = tagMapper.selectByExample(tagExample);
        //设置questionDTO的属性并返回
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        questionDTO.setTags(tags);
        return questionDTO;
    }

    /**
     * 获取问题的相关问题
     *
     * @param questionDTO
     * @return
     */
    public List<QuestionDTO> getRelatedQuestion(QuestionDTO questionDTO) {
        if ((questionDTO.getTags().size()) == 0) {//这种情况虽然不存在，但是以防万一
            return new ArrayList<>();
        }
        List<Tag> tags = questionDTO.getTags();
        String[] var1 = {"/", "/"};
        List<String> var2 = tags.stream().map(tag -> StringUtils.join(var1, tag.getId().toString())).collect(Collectors.toList());
        String regexpTag = StringUtils.join(var2, "|");
        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setTag(regexpTag);
        List<Question> relatedQuestions = questionMapperExt.selectRelatedQuestion(question);
        List<QuestionDTO> relatedQuestionDTOS = relatedQuestions.stream().map(relatedQuestion -> {
            QuestionDTO questionDTO1 = new QuestionDTO();
            BeanUtils.copyProperties(relatedQuestion, questionDTO1);
            return questionDTO1;
        }).collect(Collectors.toList());
        return relatedQuestionDTOS;
    }

    /**
     * 根据QuestionDTO对象获取其Tag对象id组成的字符串，形如:"/1/2/3/"
     *
     * @param questionDTO
     * @return
     */
    public String getTagString(QuestionDTO questionDTO) {
        List<Tag> tagList = questionDTO.getTags();
        List<String> stringList = tagList.stream().map(tag -> tag.getId().toString()).collect(Collectors.toList());
        String str1 = StringUtils.join(stringList, "/");
        String[] str2 = {"/", "/"};
        return StringUtils.join(str2, str1);
    }
}
