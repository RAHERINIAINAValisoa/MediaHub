-- Données issues de l'ancienne version PHP/JSON (importées une seule fois, si la table users est vide)

INSERT INTO users (id, nom, prenom, email, mot_de_passe, photo, role, active) VALUES ('usr_292f440dd39e', 'System', 'Admin', 'admin@mediahub.com', '$2y$10$clLTARTY8lsTiXgeaNoMSOfRnlz4lfv4fNWFvbbKmYNmeubO/QsrO', NULL, 'admin', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO users (id, nom, prenom, email, mot_de_passe, photo, role, active) VALUES ('usr_b53a66ed000e', 'Dubois', 'Marie', 'marie.dubois@example.com', '$2b$10$Fe8Fx/CYzIzyKkLAkJxFR.KSvRa7qOsizrLvIWrEWHPLiNL93iS72', NULL, 'user', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO users (id, nom, prenom, email, mot_de_passe, photo, role, active) VALUES ('usr_19324340b9d5', 'Martin', 'Julien', 'julien.martin@example.com', '$2b$10$J01GYJP/gdwHo15DUNiwiehsN5mEfKOwu7dUR15aRA4u0WSJX97EG', NULL, 'user', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO users (id, nom, prenom, email, mot_de_passe, photo, role, active) VALUES ('usr_cfc233db1133', 'Nguyen', 'Sophie', 'sophie.nguyen@example.com', '$2b$10$xcCdmmq2AThLdjEJEupbruCVcrjiOg8PBBj0PefgMredstNeomarG', NULL, 'user', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO users (id, nom, prenom, email, mot_de_passe, photo, role, active) VALUES ('usr_0d46a8ac5190', 'Bernard', 'Thomas', 'thomas.bernard@example.com', '$2b$10$fZU4L8BQOg.akTl.iyaUS.T6snEnEVwMedI8kehUKxjvmzGe/grVa', NULL, 'user', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO users (id, nom, prenom, email, mot_de_passe, photo, role, active) VALUES ('usr_b20610158499', 'RAHERINIAINA', 'VALISOA', 'valisoa@mediahub.com', '$2y$10$ODX2GMGTGJBbXwMitDR0tO3K2h3XjfpdDuYg1/3Urk/YiPuJpZoXy', 'profiles/avatar_usr_b20610158499_1784696100.jpg', 'user', TRUE) ON CONFLICT DO NOTHING;

INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_0bdfdc497959', 'Coucher de soleil sur la baie', 'Un magnifique dégradé de couleurs capturé en fin de journée.', 'image', 'medias/coucher-soleil.jpg', 'usr_b53a66ed000e', '2026-07-18 10:00:00', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_90b6f4ec5789', 'Randonnée en montagne', 'Vue panoramique depuis le sommet après trois heures de marche.', 'image', 'medias/montagnes.jpg', 'usr_19324340b9d5', '2026-07-17 10:00:00', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_3fb65a6f0162', 'Skyline urbain de nuit', 'Les lumières de la ville vues depuis les hauteurs.', 'image', 'medias/ville-nuit.jpg', 'usr_292f440dd39e', '2026-07-16 10:00:00', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_e064d97e52d5', 'Démo vidéo de présentation', 'Courte vidéo de démonstration pour tester le lecteur intégré.', 'video', 'medias/demo-video.mp4', 'usr_cfc233db1133', '2026-07-15 10:00:00', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_bf457cc46ef3', 'Piste audio de démonstration', 'Extrait sonore pour tester le lecteur audio de la plateforme.', 'audio', 'medias/demo-audio.mp3', 'usr_0d46a8ac5190', '2026-07-14 10:00:00', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_3d21e170d80e', 'ggg', '', 'video', 'medias/media_6a5fc25be61aa_1784660571.mp4', 'usr_b20610158499', '2026-07-21 21:02:51', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_b0a73112a00d', 'Alefa Nda Unity-Gasy_sFeeling', '', 'video', 'medias/media_6a5fc3da102d8_1784660954.mp4', 'usr_b20610158499', '2026-07-21 21:09:14', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_9e2f48cd92d8', 'Alefa Nda Unity-Rominah', '', 'video', 'medias/media_6a5fc415e759e_1784661013.mp4', 'usr_b20610158499', '2026-07-21 21:10:13', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_ececdd7785d2', 'cruch', '', 'image', 'medias/media_6a5fc49914d1f_1784661145.jpg', 'usr_b20610158499', '2026-07-21 21:12:25', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_64c6c45fd551', 'Star', '', 'image', 'medias/media_6a5fc4f7f027f_1784661239.jpg', 'usr_b20610158499', '2026-07-21 21:13:59', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_a6bd9bc8c645', 'cruch Miary', '', 'image', 'medias/media_6a5fc56786473_1784661351.jpg', 'usr_b20610158499', '2026-07-21 21:15:51', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_f3f7cbe2c405', 'Basta Lion- Colombian', '', 'audio', 'medias/media_6a5fc5d1edf00_1784661457.mp3', 'usr_b20610158499', '2026-07-21 21:17:37', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_31263cba583b', 'ANATAL LEO', '', 'audio', 'medias/media_6a5fc5fa71329_1784661498.mp3', 'usr_b20610158499', '2026-07-21 21:18:18', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_53ab0c4dbb37', 'Alefa Nda Unity', '', 'video', 'medias/media_6a60496e615bf_1784695150.mp4', 'usr_b20610158499', '2026-07-22 06:39:10', TRUE) ON CONFLICT DO NOTHING;
INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide) VALUES ('med_42b761f4461f', 'amis', '', 'image', 'medias/media_6a605774eba43_1784698740.jpg', 'usr_b20610158499', '2026-07-22 07:39:00', TRUE) ON CONFLICT DO NOTHING;

INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_c1762959eab1', 'med_0bdfdc497959', 'usr_19324340b9d5', 'Superbes couleurs, ça donne envie d''y être !', '2026-07-18 10:00:00') ON CONFLICT DO NOTHING;
INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_588744d6d3de', 'med_0bdfdc497959', 'usr_cfc233db1133', 'Magnifique prise de vue, quel appareil as-tu utilisé ?', '2026-07-18 07:00:00') ON CONFLICT DO NOTHING;
INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_f8f8fb7c7682', 'med_90b6f4ec5789', 'usr_292f440dd39e', 'Merci pour le partage, la vue est splendide.', '2026-07-18 04:00:00') ON CONFLICT DO NOTHING;
INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_48f8edd65ef0', 'med_e064d97e52d5', 'usr_b53a66ed000e', 'Le lecteur vidéo fonctionne très bien, bravo !', '2026-07-18 10:00:00') ON CONFLICT DO NOTHING;
INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_958b9e0d6bfa', 'med_3d21e170d80e', 'usr_b20610158499', 'nouveaute', '2026-07-21 21:03:30') ON CONFLICT DO NOTHING;
INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_26be3290467f', 'med_a6bd9bc8c645', 'usr_b20610158499', 'Manja', '2026-07-21 21:16:08') ON CONFLICT DO NOTHING;
INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_e1ca10b9da24', 'med_53ab0c4dbb37', 'usr_b20610158499', 'NORME', '2026-07-22 06:39:26') ON CONFLICT DO NOTHING;
INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES ('com_e3387d3bea4f', 'med_42b761f4461f', 'usr_b20610158499', 'cv', '2026-07-22 07:39:12') ON CONFLICT DO NOTHING;

INSERT INTO likes (user_id, media_id) VALUES ('usr_b53a66ed000e', 'med_90b6f4ec5789') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_b53a66ed000e', 'med_e064d97e52d5') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_19324340b9d5', 'med_0bdfdc497959') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_19324340b9d5', 'med_bf457cc46ef3') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_cfc233db1133', 'med_0bdfdc497959') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_cfc233db1133', 'med_3fb65a6f0162') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_0d46a8ac5190', 'med_0bdfdc497959') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_292f440dd39e', 'med_e064d97e52d5') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_b20610158499', 'med_3d21e170d80e') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_b20610158499', 'med_a6bd9bc8c645') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_b20610158499', 'med_f3f7cbe2c405') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_b20610158499', 'med_53ab0c4dbb37') ON CONFLICT DO NOTHING;
INSERT INTO likes (user_id, media_id) VALUES ('usr_b20610158499', 'med_42b761f4461f') ON CONFLICT DO NOTHING;
INSERT INTO favoris (user_id, media_id) VALUES ('usr_b53a66ed000e', 'med_e064d97e52d5') ON CONFLICT DO NOTHING;
INSERT INTO favoris (user_id, media_id) VALUES ('usr_19324340b9d5', 'med_0bdfdc497959') ON CONFLICT DO NOTHING;
INSERT INTO favoris (user_id, media_id) VALUES ('usr_cfc233db1133', 'med_90b6f4ec5789') ON CONFLICT DO NOTHING;
INSERT INTO favoris (user_id, media_id) VALUES ('usr_b20610158499', 'med_a6bd9bc8c645') ON CONFLICT DO NOTHING;
INSERT INTO favoris (user_id, media_id) VALUES ('usr_b20610158499', 'med_f3f7cbe2c405') ON CONFLICT DO NOTHING;
INSERT INTO favoris (user_id, media_id) VALUES ('usr_b20610158499', 'med_53ab0c4dbb37') ON CONFLICT DO NOTHING;
INSERT INTO favoris (user_id, media_id) VALUES ('usr_b20610158499', 'med_42b761f4461f') ON CONFLICT DO NOTHING;
