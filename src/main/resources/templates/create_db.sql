CREATE TABLE `statistics` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `vacancy_id` int(5) NOT NULL,
  `keyword` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7998 DEFAULT CHARSET=latin1;

CREATE TABLE `vacancies` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `position` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `company` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `medium_salary` int(5) NOT NULL,
  `keywords` varchar(2000) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7998 DEFAULT CHARSET=latin1;

CREATE TABLE `keywords` (
  `keyword` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
   `count` int(5) NOT NULL,
  PRIMARY KEY (`keyword`)
) ENGINE=InnoDB AUTO_INCREMENT=7998 DEFAULT CHARSET=latin1;
