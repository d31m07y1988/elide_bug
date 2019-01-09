package com.example.demo.entity;

import com.yahoo.elide.annotation.Include;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="child")
@Include(rootLevel = true)
public class Child extends BaseEntity<Child> {


}
