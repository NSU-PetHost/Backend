package NSU.PetHost.ContentService.integration.controllers;

import NSU.PetHost.ContentService.integration.BaseIntegrationTest;
import NSU.PetHost.ContentService.models.Animals;
import NSU.PetHost.ContentService.models.PersonJWT;
import NSU.PetHost.ContentService.repositories.AnimalRepository;
import NSU.PetHost.ContentService.security.PersonDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalsControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    @BeforeEach
    void setUpSecurity() {
        PersonDetails mockUser = new PersonDetails(new PersonJWT(1, "test_user", "USER"));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createAnimal_shouldSaveToDatabaseWithoutImage() throws Exception {

        mockMvc.perform(multipart("/api/v1/animals/create")
                        .param("name", "Barsik")
                        .param("dateOfBirth", "2020-01-01")
                        .param("weight", "3.5")
                        .param("petTypeId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Animal created"));

        List<Animals> saved = animalRepository.findAll();
        assertEquals(1, saved.size());
        assertEquals("Barsik", saved.get(0).getName());
        assertEquals(3.5, saved.get(0).getWeight());
    }
}