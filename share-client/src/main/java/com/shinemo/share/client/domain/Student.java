package com.shinemo.share.client.domain;

import com.shinemo.client.common.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseDO {

    private String name;

    private Integer age;

    private Integer weight;

}
