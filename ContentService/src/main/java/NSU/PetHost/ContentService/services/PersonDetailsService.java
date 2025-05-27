package NSU.PetHost.ContentService.services;

import NSU.PetHost.ContentService.security.PersonDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {

    @Override
    public PersonDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
//        return loadUserByNickname(nickname);
        return null;
    }
}
