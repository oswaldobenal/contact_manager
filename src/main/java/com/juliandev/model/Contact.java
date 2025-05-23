package com.juliandev.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String contactType;
    private boolean favorite;

    public Contact() {}

    public Contact(int id, String name, String email, String phone, String contactType, boolean favorite) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.contactType = contactType;
        this.favorite = favorite;
    }

    @JsonProperty("id")
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("email")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonProperty("phone")
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @JsonProperty("contactType")
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }

    @JsonProperty("favorite")
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}