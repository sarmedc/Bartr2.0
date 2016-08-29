package com.example.sarmedchaudhry.bartr20;


public class Users {

    private int _id;
    private String _name, _email, _password;

    Users(){}

    Users(String name, String email, String password){
        this._name = name;
        this._email = email;
        this._password = password;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_name() {
        return _name;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public String get_password() {
        return _password;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_email() {
        return _email;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }
}
