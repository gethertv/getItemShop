package dev.gether.getitemshop.user;

import dev.gether.getitemshop.service.Service;

import java.util.List;

public interface ItemShopCallback {

    public void queryDone(List<Service> services);
    public void queryAddService(boolean status);
}
