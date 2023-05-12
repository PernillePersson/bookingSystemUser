package com.example.bookingsystemuser.model;

import com.example.bookingsystemuser.model.objects.Company;
import com.example.bookingsystemuser.model.objects.Organisation;

import java.util.List;

public interface OrganisationDAO {
    public void addOrg(String bk, int o);

    public List<Organisation> getAllOrg();

    public Organisation getOrg(int id);

    public Company getCompany(int id);

    public void addCompany(String bk, String c);
}
