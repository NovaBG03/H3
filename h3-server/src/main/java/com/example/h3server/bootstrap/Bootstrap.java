package com.example.h3server.bootstrap;

import com.example.h3server.models.*;
import com.example.h3server.repositories.FamilyMemberRepository;
import com.example.h3server.repositories.FamilyTreeRepository;
import com.example.h3server.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FamilyTreeRepository familyTreeRepository;
    private final FamilyMemberRepository familyMemberRepository;

    public Bootstrap(PasswordEncoder passwordEncoder,
                     UserRepository userRepository,
                     FamilyTreeRepository familyTreeRepository,
                     FamilyMemberRepository familyMemberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.familyTreeRepository = familyTreeRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        this.loadUsers();
        this.loadFamilyTrees();
        this.loadFamilyMembers();
    }

    private void loadUsers() {

        User root = User.builder()
                .username("root")
                .email("root@root.com")
                .password(this.passwordEncoder.encode("rootroot"))
                .build();
        root.addRole(Role.ROLE_ADMIN);
        root.addRole(Role.ROLE_USER);
        this.userRepository.save(root);

        User user = User.builder()
                .username("user")
                .email("user@user.com")
                .password(this.passwordEncoder.encode("useruser"))
                .build();
        user.addRole(Role.ROLE_USER);
        this.userRepository.save(user);

        log.info("Loaded Users: " + this.userRepository.count());
    }

    private void loadFamilyTrees() {
        this.familyTreeRepository.save(FamilyTree.builder()
                .name("My First Family Tree")
                .isPrivate(true)
                .createdAt(LocalDateTime.now())
                .user(userRepository.findByUsername("root"))
                .build());

        FamilyTree queensFamilyTree = this.familyTreeRepository.save(FamilyTree.builder()
                .name("Queen's Family Tree")
                .isPrivate(false)
                .createdAt(LocalDateTime.now())
                .user(userRepository.findByUsername("root"))
                .build());

        log.info("Loaded Family Trees: " + this.familyTreeRepository.count());

        // for testing, TODO remove
        // this.familyTreeRepository.delete(queensFamilyTree);
    }

    private void loadFamilyMembers() {
        FamilyTree familyTree = this.familyTreeRepository.findById(1L).get();

        FamilyMember ivanGogov = FamilyMember.builder()
                .firstName("Ivan")
                .lastName("Gogov")
                .birthday(LocalDate.of(1955, 5, 5))
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(ivanGogov);
        familyMemberRepository.save(ivanGogov);

        FamilyMember minkaGogova = FamilyMember.builder()
                .firstName("Minka")
                .lastName("Gogova")
                .gender(Gender.FEMALE)
                .birthday(LocalDate.of(1960, 11, 9))
                .build();
        familyTree.addFamilyMember(minkaGogova);
        familyMemberRepository.save(minkaGogova);

        FamilyMember linaGogova = FamilyMember.builder()
                .firstName("Lina")
                .lastName("Gogova")
                .birthday(LocalDate.of(1998, 11, 11))
                .primaryParent(ivanGogov)
                .secondaryParent(minkaGogova)
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(linaGogova);
        familyMemberRepository.save(linaGogova);

        FamilyMember vasilGogov = FamilyMember.builder()
                .firstName("Vasil")
                .lastName("Gogov")
                .birthday(LocalDate.of(2000, 3, 25))
                .primaryParent(ivanGogov)
                .secondaryParent(minkaGogova)
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(vasilGogov);
        familyMemberRepository.save(vasilGogov);

        FamilyMember stankaGogova = FamilyMember.builder()
                .firstName("Stanka")
                .lastName("Gogova")
                .birthday(LocalDate.of(2000, 5, 16))
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(stankaGogova);
        familyMemberRepository.save(stankaGogova);

        FamilyMember georgiGogov = FamilyMember.builder()
                .firstName("Georgi")
                .lastName("Gogov")
                .birthday(LocalDate.of(2020, 2, 15))
                .primaryParent(vasilGogov)
                .secondaryParent(stankaGogova)
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(georgiGogov);
        familyMemberRepository.save(georgiGogov);

        FamilyMember petkoPetkov = FamilyMember.builder()
                .firstName("Petko")
                .lastName("Petkov")
                .birthday(LocalDate.of(1995, 12, 29))
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(petkoPetkov);
        familyMemberRepository.save(petkoPetkov);

        FamilyMember lilPetko = FamilyMember.builder()
                .firstName("Lil")
                .lastName("Petko")
                .birthday(LocalDate.of(2020, 9, 22))
                .primaryParent(linaGogova)
                .secondaryParent(petkoPetkov)
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(lilPetko);
        familyMemberRepository.save(lilPetko);

        log.info("Loaded Family Members: " + this.familyMemberRepository.count());
    }
}
