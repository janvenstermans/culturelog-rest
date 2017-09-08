package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.domain.User;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link UserController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
@WebAppConfiguration
public class UserControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    private static final String URL_USERS_REGISTER = "/users/register ";

    // users/register OPTIONS

    @Test
    public void testLocationsUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_USERS_REGISTER, HttpMethod.POST);
    }

    // users/register POST

    @Test
    public void testRegisterUser_userNameIsEmailAdress() throws Exception {
        String userName = "abv@b.cd";
        String passwordNotEncoded = "password";
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(userName);
        userCreateDto.setPassword(passwordNotEncoded);

        mockMvc.perform(post(URL_USERS_REGISTER)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        User user = userRepository.findByUsername(userName);
        assertNotNull(user);
        assertTrue(user.isActive());
        assertNotEquals(passwordNotEncoded, user.getPassword());
    }

    @Test
    public void testRegisterUser_userNameIsNotEmailAdress() throws Exception {
        String userName = "abcd";
        String passwordNotEncoded = "password";
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(userName);
        userCreateDto.setPassword(passwordNotEncoded);

        mockMvc.perform(post(URL_USERS_REGISTER)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().is4xxClientError());

        User user = userRepository.findByUsername(userName);
        assertNull(user);
    }

    // TODO: create test
    // username already exists
    // not all info availale
}