package model;

public class Courses {
    private Long id;
    private String name;
    private String credit;
    private Long DeptId;

    public Courses() {
    }

    public Courses(Long id, String name, String credit, Long deptId) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        DeptId = deptId;
    }

    public Courses(String name, String credit, Long deptId) {
        this.name = name;
        this.credit = credit;
        DeptId = deptId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public Long getDeptId() {
        return DeptId;
    }

    public void setDeptId(Long deptId) {
        DeptId = deptId;
    }
}