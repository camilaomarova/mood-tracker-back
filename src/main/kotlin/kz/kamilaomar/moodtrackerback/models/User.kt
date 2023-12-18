package kz.kamilaomar.moodtrackerback.models

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,

    @Column(unique = true) // Add unique constraint to email
    val email: String? = null,

    val username: String? = null,
    val password: String? = null, // TODO: use password hashing

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Set<Role> = emptySet()
)
