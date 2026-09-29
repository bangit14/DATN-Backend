package com.backend.recruitmentservice.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("industries")
public class Industry {
    @TableId(type = IdType.ASSIGN_UUID)
    private UUID id;

    @TableField("name")
    private String name;

    @TableField("slug")
    private String slug;
}
