
CREATE TABLE IF NOT EXISTS `headers` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `domain` varchar(255) NOT NULL,
  `urlPrefix` varchar(255) NOT NULL,
  `header` text NOT NULL,
  `headerValue` text NOT NULL,
  `mode` varchar(20) NOT NULL COMMENT 'APPEND,REPLACE,ADD',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `replacebody`
--

CREATE TABLE IF NOT EXISTS `replacebody` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `urlKey` varchar(255) DEFAULT NULL,
  `jsonPath` varchar(255) NOT NULL,
  `find` varchar(255) NOT NULL,
  `replace` varchar(255) NOT NULL,
  `needURLDecodeRest` int(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;