package com.xmcc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

//设置为true,表示update对象的时候,生成动态的update语句,如果这个字段的值是null就不会被加入到update语句中
@DynamicUpdate
@Data //相当于get,set tostring
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student")
@Entity
public class Student implements Serializable {
    @Id//主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //表示自增IDENTITY：mysql SEQUENCE:oracle  //主键生成策略
    private Integer id;

    private String username;

    private String gender;

    private Integer score;
}
