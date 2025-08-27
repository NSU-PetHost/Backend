package NSU.PetHost.ContentService.unit;

import NSU.PetHost.ContentService.dto.responses.positive.AnimalResponse;
import NSU.PetHost.ContentService.dto.responses.positive.OkResponse;
import NSU.PetHost.ContentService.exceptions.animals.AnimalNotFoundException;
import NSU.PetHost.ContentService.models.Animals;
import NSU.PetHost.ContentService.models.AnimalsTypes;
import NSU.PetHost.ContentService.models.Images;
import NSU.PetHost.ContentService.models.PersonJWT;
import NSU.PetHost.ContentService.repositories.AnimalRepository;
import NSU.PetHost.ContentService.repositories.StatisticsRepository;
import NSU.PetHost.ContentService.security.PersonDetails;
import NSU.PetHost.ContentService.services.AnimalsService;
import NSU.PetHost.ContentService.services.AnimalsTypesService;
import NSU.PetHost.ContentService.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class AnimalsServiceUT {


    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private AnimalsTypesService animalsTypesService;

    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private AnimalsService animalsService;

    @BeforeEach
    public void setUp() {
        // Мокаем SecurityContext
        PersonDetails mockUser = new PersonDetails(new PersonJWT(1, "be9sh", "USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void createAnimal_shouldSaveAnimalWithCorrectFields() {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Images images = new Images();
        images.setId(1L);
        images.setName("cat.jpg");
        images.setOwnerID(1L);
        images.setFilePath("/uploads/cat.jpg");
        images.setPrivacy(true);
        Mockito.when(imageService.uploadImage(mockFile, true))
                .thenReturn(images);
        Mockito.when(animalsTypesService.getById(1L))
                .thenReturn(new AnimalsTypes("Cat"));

        // Создаём ловушку
        ArgumentCaptor<Animals> captor = ArgumentCaptor.forClass(Animals.class);

        // when
        animalsService.createAnimal("Misha", LocalDate.of(2020, 1, 1), 3.5, mockFile, 1L);

        // then
        verify(animalRepository).save(captor.capture());
        Animals saved = captor.getValue();

        assertEquals("Misha", saved.getName());
        assertEquals(1L, saved.getOwnerId());
        assertEquals(3.5, saved.getWeight());
        assertEquals(LocalDate.of(2020, 1, 1), saved.getDateOfBirth());
        assertEquals("Cat", saved.getAnimalsType().getName());
    }

//    @Test
//    void getAnimalsTypes() {
//    }

    @Test
    void updateAnimal_shouldUpdateFields() {
        Animals animal = new Animals();
        animal.setId(1L);
        animal.setOwnerId(1L);
        animal.setName("OldName");
        animal.setWeight(3.0);
        animal.setDateOfBirth(LocalDate.of(2020, 1, 1));
        animal.setAnimalsType(new AnimalsTypes("Cat"));

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(animalsTypesService.getById(2L)).thenReturn(new AnimalsTypes("Dog"));

        OkResponse response = animalsService.updateAnimal(
                1L, "NewName", LocalDate.of(2021, 1, 1), 4.5, null, 2L
        );

        assertEquals("Animal updated", response.message());
        assertEquals("NewName", animal.getName());
        assertEquals(4.5, animal.getWeight());
        assertEquals("Dog", animal.getAnimalsType().getName());
        verify(animalRepository, Mockito.times(1)).save(animal);
    }

    @Test
    void getAnimal_shouldReturnAnimal_whenExists() {
        // given
        Animals animal = new Animals();
        animal.setId(1L);
        animal.setName("Barsik");

        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));

        // when
        Animals result = animalsService.getAnimal(1L);

        // then
        assertEquals("Barsik", result.getName());
        verify(animalRepository).findById(1L);
    }

    @Test
    void getAnimal_shouldThrow_whenNotFound() {
        when(animalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AnimalNotFoundException.class,
                () -> animalsService.getAnimal(999L));
    }

    @Test
    void findAllAnimalsByOwnerId_shouldReturnList() {
        // given
        Animals cat = new Animals();
        cat.setId(1L);
        cat.setName("Cat");

        Animals dog = new Animals();
        dog.setId(2L);
        dog.setName("Dog");

        when(animalRepository.findAnimalsByOwnerId(1L))
                .thenReturn(List.of(cat, dog));

        // when
        List<Animals> result = animalsService.findAllAnimalsByOwnerId(1L);

        // then
        assertEquals(2, result.size());
        verify(animalRepository).findAnimalsByOwnerId(1L);
    }

    @Test
    void getPets_shouldReturnOnlyCurrentUserAnimals() {
        // given
        Animals animal = new Animals();
        animal.setId(1L);
        animal.setName("Murka");
        animal.setOwnerId(1L);
        animal.setWeight(2.5);
        animal.setDateOfBirth(LocalDate.of(2022, 1, 1));

        AnimalsTypes type = new AnimalsTypes("Cat");
        animal.setAnimalsType(type);

        Images image = new Images();
        image.setId(10L);
        image.setName("cat.jpg");
        image.setOwnerID(1L);
        image.setFilePath("/uploads/cat.jpg");
        image.setPrivacy(true);
        animal.setImage(image);

        when(animalRepository.findAnimalsByOwnerId(1L)).thenReturn(List.of(animal));

        // when
        List<AnimalResponse> result = animalsService.getPets();

        // then
        assertEquals(1, result.size());
        AnimalResponse resp = result.getFirst();
        assertEquals(1L, resp.id());
        assertEquals("Cat", resp.animalType());
        assertEquals("Murka", resp.name());
        assertEquals(LocalDate.of(2022, 1, 1), resp.dateOfBirth());
        assertEquals(2.5, resp.weight());
        assertEquals(10L, resp.imageID());
    }
}
