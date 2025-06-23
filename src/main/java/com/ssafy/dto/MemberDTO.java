package com.ssafy.dto;

public class MemberDTO {
    private int mno;
    private String id;
    private String pw;
    private String name;
    private String email;
    private String role = "user";

    // 기본 생성자
    public MemberDTO() {
        super();
    }

    // mno 제외, role 제외 생성자
    public MemberDTO(String id, String pw, String name, String email) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
    }

    // mno 제외, role 포함 생성자
    public MemberDTO(String id, String pw, String name, String email, String role) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // 모든 필드 생성자
    public MemberDTO(int mno, String id, String pw, String name, String email, String role) {
        this.mno = mno;
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // getters & setters
    public int getMno() {
        return mno;
    }

    public void setMno(int mno) {
        this.mno = mno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // toString()
    @Override
    public String toString() {
        return "MemberDTO [mno=" + mno
             + ", id=" + id
             + ", pw=" + pw
             + ", name=" + name
             + ", email=" + email
             + ", role=" + role
             + "]";
    }

    // Builder 패턴 지원
    public static MemberDTOBuilder builder() {
        return new MemberDTOBuilder();
    }

    public static class MemberDTOBuilder {
        private int mno;
        private String id;
        private String pw;
        private String name;
        private String email;
        private String role = "user";

        MemberDTOBuilder() {
        }

        public MemberDTOBuilder mno(int mno) {
            this.mno = mno;
            return this;
        }

        public MemberDTOBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MemberDTOBuilder pw(String pw) {
            this.pw = pw;
            return this;
        }

        public MemberDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MemberDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public MemberDTOBuilder role(String role) {
            this.role = role;
            return this;
        }

        public MemberDTO build() {
            MemberDTO dto = new MemberDTO(mno, id, pw, name, email, role);
            return dto;
        }

        @Override
        public String toString() {
            return "MemberDTOBuilder(mno=" + mno
                 + ", id=" + id
                 + ", pw=" + pw
                 + ", name=" + name
                 + ", email=" + email
                 + ", role=" + role
                 + ")";
        }
    }
}
