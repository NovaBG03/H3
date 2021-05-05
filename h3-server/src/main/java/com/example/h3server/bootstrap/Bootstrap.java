package com.example.h3server.bootstrap;

import com.example.h3server.models.*;
import com.example.h3server.repositories.*;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

@Slf4j
@Component
public class Bootstrap implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FamilyTreeRepository familyTreeRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final TreeTagRepository treeTagRepository;
    private final CoupleRepository coupleRepository;
    private final FactRepository factRepository;

    public Bootstrap(PasswordEncoder passwordEncoder,
                     UserRepository userRepository,
                     FamilyTreeRepository familyTreeRepository,
                     FamilyMemberRepository familyMemberRepository,
                     TreeTagRepository treeTagRepository,
                     CoupleRepository coupleRepository,
                     FactRepository factRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.familyTreeRepository = familyTreeRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.treeTagRepository = treeTagRepository;
        this.coupleRepository = coupleRepository;
        this.factRepository = factRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (this.userRepository.count() == 0) {
            this.loadUsers();
            this.loadFamilyTrees();
            this.loadFamilyMembers2();
            this.loadFacts();
            // this.loadFamilyMembers();
            // this.loadTerterFamily();
        }
    }

    private void loadFacts() {
        FamilyTree tree = this.familyTreeRepository.findById(1L).get();
        FamilyMember member = this.familyMemberRepository.findByIdAndFamilyTreeId(1L, 1L);

        Fact houseFact = Fact.builder()
                .name("Bought House")
                .description("We all know it. It's been part of the family for so long.")
                .familyTree(tree)
                .familyMember(member)
                .build();

        this.factRepository.save(houseFact);

        log.info("Loaded Facts: " + this.factRepository.count());
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
        TreeTag firstTag = treeTagRepository.save(TreeTag.builder().label("first-tree").build());
        TreeTag rootTag = treeTagRepository.save(TreeTag.builder().label("root-tree").build());
        TreeTag queenTag = treeTagRepository.save(TreeTag.builder().label("queen").build());
        TreeTag royalTag = treeTagRepository.save(TreeTag.builder().label("royal").build());
        TreeTag coolTag = treeTagRepository.save(TreeTag.builder().label("cool").build());
        TreeTag amazingTag = treeTagRepository.save(TreeTag.builder().label("amazing").build());
        TreeTag userTag = treeTagRepository.save(TreeTag.builder().label("user-tree").build());

        this.familyTreeRepository.save(FamilyTree.builder()
                .name("My First Family Tree")
                .isPrivate(true)
                .createdAt(LocalDateTime.of(2020, Month.AUGUST, 10, 13, 25))
                .user(userRepository.findByUsername("root"))
                .tags(Sets.newHashSet(firstTag, rootTag, amazingTag, coolTag, rootTag, queenTag))
                .build());

        this.familyTreeRepository.save(FamilyTree.builder()
                .name("Queen's Family Tree")
                .isPrivate(false)
                .createdAt(LocalDateTime.of(2020, Month.NOVEMBER, 27, 22, 47))
                .user(userRepository.findByUsername("root"))
                .tags(Sets.newHashSet(queenTag, royalTag))
                .build());

        this.familyTreeRepository.save(FamilyTree.builder()
                .name("User's Family Tree")
                .isPrivate(false)
                .createdAt(LocalDateTime.of(2020, Month.NOVEMBER, 28, 12, 23))
                .user(userRepository.findByUsername("user"))
                .tags(Sets.newHashSet(coolTag, amazingTag, userTag))
                .build());

        log.info("Loaded Family Trees: " + this.familyTreeRepository.count());
        log.info("Loaded Tree Tags: " + this.treeTagRepository.count());

    }

    private void loadFamilyMembers2() {
        FamilyTree familyTree = this.familyTreeRepository.findById(1L).get();

        FamilyMember ivanGogov = FamilyMember.builder()
                .firstName("Ivan")
                .lastName("Gogov")
                .birthday(LocalDate.of(1955, 5, 5))
                .gender(Gender.MALE)
                .build();
        familyMemberRepository.save(ivanGogov);

        FamilyMember minkaGogova = FamilyMember.builder()
                .firstName("Minka")
                .lastName("Gogova")
                .gender(Gender.FEMALE)
                .birthday(LocalDate.of(1960, 11, 9))
                .build();
        familyMemberRepository.save(minkaGogova);

        coupleRepository.save(Couple.builder()
                .primaryParentId(ivanGogov.getId())
                .partnerParentId(minkaGogova.getId())
                .leftIndex(1)
                .rightIndex(8)
                .familyTree(familyTree)
                .build());

        FamilyMember linaGogova = FamilyMember.builder()
                .firstName("Lina")
                .lastName("Gogova")
                .birthday(LocalDate.of(1998, 11, 11))
                .gender(Gender.FEMALE)
                .build();
        familyMemberRepository.save(linaGogova);

        coupleRepository.save(Couple.builder()
                .primaryParentId(linaGogova.getId())
                .partnerParentId(0L)
                .leftIndex(2)
                .rightIndex(3)
                .familyTree(familyTree)
                .build());

        FamilyMember vasilGogov = FamilyMember.builder()
                .firstName("Vasil")
                .lastName("Gogov")
                .birthday(LocalDate.of(2000, 3, 25))
                .gender(Gender.MALE)
                .build();
        familyMemberRepository.save(vasilGogov);

        FamilyMember stankaGogova = FamilyMember.builder()
                .firstName("Stanka")
                .lastName("Gogova")
                .birthday(LocalDate.of(2000, 5, 16))
                .gender(Gender.FEMALE)
                .build();
        familyMemberRepository.save(stankaGogova);

        coupleRepository.save(Couple.builder()
                .primaryParentId(vasilGogov.getId())
                .partnerParentId(stankaGogova.getId())
                .leftIndex(4)
                .rightIndex(7)
                .familyTree(familyTree)
                .build());

        FamilyMember georgiGogov = FamilyMember.builder()
                .firstName("Georgi")
                .lastName("Gogov")
                .birthday(LocalDate.of(2020, 2, 15))
                .gender(Gender.MALE)
                .build();
        familyMemberRepository.save(georgiGogov);

        coupleRepository.save(Couple.builder()
                .primaryParentId(georgiGogov.getId())
                .partnerParentId(0L)
                .leftIndex(5)
                .rightIndex(6)
                .familyTree(familyTree)
                .build());

        log.info("Loaded Family Members: " + this.familyMemberRepository.count());
    }

    /*
    private void loadTerterFamily() {
        FamilyTree familyTree = FamilyTree.builder()
                .name("Династия на тертеревци")
                .isPrivate(false)
                .createdAt(LocalDateTime.of(2020, Month.NOVEMBER, 3, 9, 2))
                .user(userRepository.findByUsername("root"))
                .build();
        this.familyTreeRepository.save(familyTree);
        FamilyMember unknownFather = FamilyMember.builder()
                .firstName("Неизвестен")
                .lastName("Баща")
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(unknownFather);
        familyMemberRepository.save(unknownFather);
        FamilyMember unknownMother = FamilyMember.builder()
                .firstName("Неизвестен")
                .lastName("Майка")
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(unknownMother);
        familyMemberRepository.save(unknownMother);
        FamilyMember georgiTerter1 = FamilyMember.builder()
                .firstName("Георги I")
                .lastName("Тертер")
                .gender(Gender.MALE)
                .primaryParent(unknownFather)
                .secondaryParent(unknownMother)
                .build();
        familyTree.addFamilyMember(georgiTerter1);
        familyMemberRepository.save(georgiTerter1);
        FamilyMember maria = FamilyMember.builder()
                .firstName("Мария")
                .lastName("(Българка)")
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(maria);
        familyMemberRepository.save(maria);
        FamilyMember teodorSvetoslav = FamilyMember.builder()
                .firstName("Теодор")
                .lastName("Светослав")
                .gender(Gender.MALE)
                .primaryParent(georgiTerter1)
                .secondaryParent(maria)
                .build();
        familyTree.addFamilyMember(teodorSvetoslav);
        familyMemberRepository.save(teodorSvetoslav);
        FamilyMember unknownChild = FamilyMember.builder()
                .firstName("Неизвестно")
                .lastName("дете")
                .gender(Gender.UNKNOWN)
                .primaryParent(georgiTerter1)
                .secondaryParent(maria)
                .build();
        familyTree.addFamilyMember(unknownChild);
        familyMemberRepository.save(unknownChild);
        FamilyMember efrosina = FamilyMember.builder()
                .firstName("Ефросина")
                .lastName("(Енкона)")
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(efrosina);
        familyMemberRepository.save(efrosina);
        FamilyMember georgiTerter2 = FamilyMember.builder()
                .firstName("Георги II")
                .lastName("Тертер")
                .gender(Gender.MALE)
                .primaryParent(teodorSvetoslav)
                .secondaryParent(efrosina)
                .build();
        familyTree.addFamilyMember(georgiTerter2);
        familyMemberRepository.save(georgiTerter2);
        FamilyMember teodoraPaleologina = FamilyMember.builder()
                .firstName("Теодора")
                .lastName("Палеологина")
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(teodoraPaleologina);
        familyMemberRepository.save(teodoraPaleologina);
        FamilyMember emptyChild = FamilyMember.builder()
                .firstName("Нямат")
                .lastName("Деца")
                .gender(Gender.UNKNOWN)
                .primaryParent(teodorSvetoslav)
                .secondaryParent(teodoraPaleologina)
                .build();
        familyTree.addFamilyMember(emptyChild);
        familyMemberRepository.save(emptyChild);
        FamilyMember kiraMaria = FamilyMember.builder()
                .firstName("Кира")
                .lastName("Мария")
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(kiraMaria);
        familyMemberRepository.save(kiraMaria);
        FamilyMember annaTerter = FamilyMember.builder()
                .firstName("Анна")
                .lastName("Тертер")
                .gender(Gender.FEMALE)
                .primaryParent(georgiTerter1)
                .secondaryParent(kiraMaria)
                .build();
        familyTree.addFamilyMember(annaTerter);
        familyMemberRepository.save(annaTerter);
        FamilyMember stefanUroshMilutin2 = FamilyMember.builder()
                .firstName("Стефан Урош II")
                .lastName("Милутин")
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(stefanUroshMilutin2);
        familyMemberRepository.save(stefanUroshMilutin2);
        FamilyMember emptyChild2 = FamilyMember.builder()
                .firstName("Нямат")
                .lastName("Деца")
                .gender(Gender.UNKNOWN)
                .primaryParent(annaTerter)
                .secondaryParent(stefanUroshMilutin2)
                .build();
        familyTree.addFamilyMember(emptyChild2);
        familyMemberRepository.save(emptyChild2);
        FamilyMember mihailDuka = FamilyMember.builder()
                .firstName("Михаил")
                .lastName("Дука")
                .gender(Gender.MALE)
                .build();
        familyTree.addFamilyMember(mihailDuka);
        familyMemberRepository.save(mihailDuka);
        FamilyMember unknownChild2 = FamilyMember.builder()
                .firstName("Неизвестно")
                .lastName("Дете")
                .gender(Gender.UNKNOWN)
                .primaryParent(annaTerter)
                .secondaryParent(mihailDuka)
                .build();
        familyTree.addFamilyMember(unknownChild2);
        familyMemberRepository.save(unknownChild2);
        FamilyMember unknownChild3 = FamilyMember.builder()
                .firstName("Неизвестно")
                .lastName("Дете")
                .gender(Gender.UNKNOWN)
                .primaryParent(annaTerter)
                .secondaryParent(mihailDuka)
                .build();
        familyTree.addFamilyMember(unknownChild3);
        familyMemberRepository.save(unknownChild3);
        FamilyMember eltimirDespot = FamilyMember.builder()
                .firstName("Елтимир")
                .lastName("(деспот)")
                .gender(Gender.MALE)
                .primaryParent(unknownFather)
                .secondaryParent(unknownMother)
                .build();
        familyTree.addFamilyMember(eltimirDespot);
        familyMemberRepository.save(eltimirDespot);
        FamilyMember mariaSmilecova = FamilyMember.builder()
                .firstName("Мария")
                .lastName("Смилецова")
                .gender(Gender.FEMALE)
                .build();
        familyTree.addFamilyMember(mariaSmilecova);
        familyMemberRepository.save(mariaSmilecova);
        FamilyMember ioanDragushin = FamilyMember.builder()
                .firstName("Йоан")
                .lastName("Драгушин")
                .gender(Gender.MALE)
                .primaryParent(eltimirDespot)
                .secondaryParent(mariaSmilecova)
                .build();
        familyTree.addFamilyMember(ioanDragushin);
        familyMemberRepository.save(ioanDragushin);
        log.info("Loaded Terter Family");
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


        FamilyTree userTree = this.familyTreeRepository.findById(3L).get();

        FamilyMember peshoPeshev = FamilyMember.builder()
                .firstName("Pesho")
                .lastName("Peshev")
                .birthday(LocalDate.of(1955, 5, 5))
                .gender(Gender.MALE)
                .build();
        userTree.addFamilyMember(peshoPeshev);
        familyMemberRepository.save(peshoPeshev);

        FamilyMember sashkaPesheva = FamilyMember.builder()
                .firstName("Sashka")
                .lastName("Pesheva")
                .gender(Gender.FEMALE)
                .birthday(LocalDate.of(1960, 11, 9))
                .build();
        userTree.addFamilyMember(sashkaPesheva);
        familyMemberRepository.save(sashkaPesheva);

        FamilyMember liliqPesheva = FamilyMember.builder()
                .firstName("Liliq")
                .lastName("Pesheva")
                .birthday(LocalDate.of(1998, 11, 11))
                .primaryParent(peshoPeshev)
                .secondaryParent(sashkaPesheva)
                .gender(Gender.FEMALE)
                .build();
        userTree.addFamilyMember(liliqPesheva);
        familyMemberRepository.save(liliqPesheva);

        FamilyMember vilioPeshev = FamilyMember.builder()
                .firstName("Vilio")
                .lastName("Peshev")
                .birthday(LocalDate.of(2000, 3, 25))
                .primaryParent(peshoPeshev)
                .secondaryParent(sashkaPesheva)
                .gender(Gender.MALE)
                .build();
        userTree.addFamilyMember(vilioPeshev);
        familyMemberRepository.save(vilioPeshev);

        log.info("Loaded Family Members: " + this.familyMemberRepository.count());
    }
    */
}
