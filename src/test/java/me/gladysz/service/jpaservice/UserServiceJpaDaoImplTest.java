package me.gladysz.service.jpaservice;

import me.gladysz.model.Customer;
import me.gladysz.model.User;
import me.gladysz.service.CustomerService;
import me.gladysz.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("jpadao")
public class UserServiceJpaDaoImplTest {

    private UserService userService;

    private CustomerService customerService;

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Test
    public void shouldListAllUsers() throws Exception {
        List<User> users = (List<User>) userService.listAll();

        assertThat(users.size()).isEqualTo(3);
    }

    @Test
    public void shouldGetOneById() throws Exception {
        User user = userService.getById(1L);

        assertThat(user).isNotNull();
        assertThat(user).hasFieldOrPropertyWithValue("username", "Username1");
        assertThat(user).hasFieldOrPropertyWithValue("password", null);
        assertThat(user).hasFieldOrPropertyWithValue("version", 0);
        assertThat(user.getEncryptedPassword()).isNotEmpty();
        assertThat(user.getCustomer()).isNotNull();
    }

    @Test
    @DirtiesContext
    public void shouldAddNewUser() throws Exception {
        User user = new User();

        user.setUsername("someusername");
        user.setPassword("mypassword");

        User savedUser = userService.saveOrUpdate(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEncryptedPassword()).isNotNull();

        System.out.println("Encrypted Password");
        System.out.println(savedUser.getEncryptedPassword());
    }

    @Test
    @DirtiesContext
    public void shouldSaveUserWithCustomer() throws Exception {
        User user = new User();

        user.setUsername("username2");
        user.setPassword("password2");

        Customer customer = new Customer();
        customer.setFirstName("FirstName");
        customer.setLastName("LastName");

        user.setCustomer(customer);

        User savedUser = userService.saveOrUpdate(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEncryptedPassword()).isNotNull();
        assertThat(savedUser.getPassword()).isNull();
        assertThat(savedUser.getVersion()).isNotNull();
        assertThat(savedUser.getCustomer()).isNotNull();
        assertThat(savedUser.getCustomer().getId()).isNotNull();
        assertThat(savedUser.getCustomer().getVersion()).isNotNull();
    }

    @Test
    @DirtiesContext
    public void shouldDeleteOne() throws Exception {
        List<User> userListBeforeDelete = (List<User>) userService.listAll();
        assertThat(userListBeforeDelete.size()).isEqualTo(3);

        userService.delete(1L);

        List<User> userListAfterDelete = (List<User>) userService.listAll();
        assertThat(userListAfterDelete.size()).isEqualTo(2);
    }

    @Test
    @DirtiesContext
    public void shouldDeleteOneAndKeepCustomer() throws Exception {
        List<User> userListBeforeDelete = (List<User>) userService.listAll();
        assertThat(userListBeforeDelete.size()).isEqualTo(3);

        List<User> customerListBeforeDelete = (List<User>) customerService.listAll();
        assertThat(customerListBeforeDelete.size()).isEqualTo(3);

        userService.delete(1L);

        List<User> userListAfterDelete = (List<User>) userService.listAll();
        assertThat(userListAfterDelete.size()).isEqualTo(2);

        List<User> customerListAfterDelete = (List<User>) customerService.listAll();
        assertThat(customerListAfterDelete.size()).isEqualTo(3);
    }

    @Test
    @DirtiesContext
    public void shouldDeleteOneWithoutCustomer() throws Exception {
        List<User> userListBeforeDelete = (List<User>) userService.listAll();
        assertThat(userListBeforeDelete.size()).isEqualTo(3);

        userService.delete(3L);

        List<User> userListAfterDelete = (List<User>) userService.listAll();
        assertThat(userListAfterDelete.size()).isEqualTo(2);
    }
}