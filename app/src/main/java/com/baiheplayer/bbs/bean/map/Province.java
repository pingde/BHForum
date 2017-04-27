package com.baiheplayer.bbs.bean.map;

import com.baiheplayer.bbs.bean.map.City;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */
@Table(name = "Province")
public class Province implements Serializable {
    @Column(name = "index",isId = true)
    private int index;
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "pid")
    private String pid;
    @Column(name = "child")
    private List<City> child;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<City> getChild() {
        return child;
    }

    public void setChild(List<City> child) {
        this.child = child;
    }

}
