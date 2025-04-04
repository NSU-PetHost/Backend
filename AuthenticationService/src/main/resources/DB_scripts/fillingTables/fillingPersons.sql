
-- all users
insert into authorities (authority_name) values ('SEE POSTS');
insert into authorities (authority_name) values ('SEE COMMENTS TO THE POSTS');

-- authorize users
insert into authorities (authority_name) values ('SEE PERSONAL ACCOUNT');
insert into authorities (authority_name) values ('SEE PERSONAL STATS');
insert into authorities (authority_name) values ('GET PERSONALIZED RECOMMENDS');
insert into authorities (authority_name) values ('ADD PETS');
insert into authorities (authority_name) values ('CREATE NOTES FOR THE YOUR PET');
insert into authorities (authority_name) values ('COMMENT POSTS');
insert into authorities (authority_name) values ('WRITE POSTS');
insert into authorities (authority_name) values ('GET NOTIFICATIONS');
insert into authorities (authority_name) values ('ADD IN FAMILY');

-- типа умный чел
insert into authorities (authority_name) values ('VERIFY AUTHENTICITY POSTS');

-- moderator
insert into authorities (authority_name) values ('SEE LIST OF POSTS SUBMITTED FOR MODERATION');
insert into authorities (authority_name) values ('PUBLISH POSTS');
insert into authorities (authority_name) values ('REJECT PUBLISH POSTS');
insert into authorities (authority_name) values ('CHANGE AUTHORITY OTHER USERS');
insert into authorities (authority_name) values ('BAN USERS');