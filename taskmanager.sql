-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1:3306
-- Время создания: Май 02 2019 г., 12:39
-- Версия сервера: 5.5.53
-- Версия PHP: 5.6.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `taskmanager`
--

-- --------------------------------------------------------

--
-- Структура таблицы `auth`
--

CREATE TABLE `auth` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `access_token` varchar(255) NOT NULL,
  `access_expires_in` int(11) NOT NULL,
  `refresh_token` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `auth`
--

INSERT INTO `auth` (`id`, `id_user`, `access_token`, `access_expires_in`, `refresh_token`) VALUES
(1, 1, '16e1c30c6abcb9c9edf2b5b4dc6e54f241f1b5570bc21c7179b48e70b770bc93', 1556756193, ''),
(5, 2, 'baf9a897f44b2fd774c1031d7349f39b8039db99f77de5fe8488ec9cdd88ed85', 1556495751, ''),
(6, 3, '57df138d0f5c63382a723c04d9f7913cfa8232b087f9ad3d408683abcd155334', 1556496849, ''),
(7, 4, '80cff732ac4ca0cd251d9e8184227698d1d27c47a20a9c773395fead2f2c1cb1', 1555458522, '');

-- --------------------------------------------------------

--
-- Структура таблицы `task`
--

CREATE TABLE `task` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `date_created` date NOT NULL,
  `date_deadline` date NOT NULL,
  `id_user` int(11) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `progress` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `task`
--

INSERT INTO `task` (`id`, `title`, `description`, `date_created`, `date_deadline`, `id_user`, `status`, `progress`) VALUES
(1, 'Разработать модуль для авторизации пользователей ', 'Необходимо реализовать модуль для авторизации пользователей через Андроид клиент с RSA шифрованием.', '2019-04-11', '2019-04-20', 1, 1, 75),
(3, 'kjhkjhj', 'jhkjhkjh', '2019-04-13', '2020-02-20', 1, 0, 0),
(4, 'jhkjhk', 'hkjhkhkjh', '2019-04-14', '2019-02-20', 1, 0, 0),
(5, 'ioiopiopi', 'iopipiouokj', '2019-04-14', '2019-02-03', 1, 0, 0),
(6, 'iuouiui', 'uiuiuiuiui', '2019-04-14', '2019-02-03', 1, 0, 0),
(8, 'task 10', 'description task 10', '2019-04-14', '2019-02-07', 1, 0, 0),
(9, 'task 10', 'description 10', '2019-04-14', '2019-02-21', 1, 0, 100),
(10, '\nNew task edited', 'New description edit', '2019-04-24', '2019-03-27', 1, 0, 38),
(11, 'create task manager', 'We need to create task manager on Android platform', '2019-04-14', '2019-02-08', 1, 0, 100),
(12, 'Новая задача1', 'Проверка новой задачи через телкфон samsung A3', '2019-04-14', '2019-04-21', 1, 0, 100),
(13, 'hello', 'fine', '2019-04-15', '2019-02-15', 1, 0, 25),
(14, 'hghjhj', 'jhgkhjgkjgkhjg', '2019-04-15', '2024-02-13', 1, 0, 0),
(15, 'efenjhb', 'jbhjbjbj', '2019-04-18', '2019-02-15', 1, 0, 100),
(16, 'TASSSSSK Edited2', 'jsdfdsfdsf', '2019-04-18', '2019-04-17', 1, 0, 100),
(17, '11111', '111111', '2019-04-18', '2019-04-16', 1, 0, 88),
(21, 'new task', 'hello', '2019-04-23', '2019-02-28', 1, 0, 0),
(23, 'kmkljklj', 'kljlkjkl', '2019-04-24', '1998-11-27', 1, 0, 25);

-- --------------------------------------------------------

--
-- Структура таблицы `task_chat`
--

CREATE TABLE `task_chat` (
  `id` int(11) NOT NULL,
  `id_task` int(11) NOT NULL,
  `id_sender` int(11) NOT NULL,
  `text` text NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `task_chat`
--

INSERT INTO `task_chat` (`id`, `id_task`, `id_sender`, `text`, `date`) VALUES
(1, 1, 1, 'dsdsf', '2019-04-27 11:15:15'),
(2, 1, 1, 'hello', '2019-04-27 11:28:44'),
(3, 1, 1, 'hello world!!!!!!!!!!!!', '2019-04-27 11:28:50'),
(4, 1, 2, 'Другой чувак написал', '2019-04-27 11:29:50'),
(5, 1, 1, ' how are you&', '2019-04-27 11:35:14'),
(6, 1, 2, 'oh no', '2019-04-27 11:35:21'),
(7, 1, 1, 'dsfdsfds', '2019-04-27 11:40:06'),
(8, 1, 1, 'he', '2019-04-27 11:42:38'),
(9, 1, 2, 'uhdfdf', '2019-04-27 11:42:45'),
(10, 1, 1, 'fdgfdg', '2019-04-27 11:45:16'),
(11, 1, 2, 'dfdf', '2019-04-27 11:45:33'),
(12, 1, 1, 'dsf', '2019-04-27 11:46:12'),
(13, 1, 1, 'dsfdsf', '2019-04-27 11:46:20'),
(14, 1, 2, 'dsfd', '2019-04-27 11:46:44'),
(15, 1, 2, 'dfdsf', '2019-04-27 11:51:08'),
(16, 1, 1, 'goooo', '2019-04-27 11:51:42'),
(17, 1, 2, 'fdgfdgfd', '2019-04-27 11:51:54'),
(18, 1, 1, 'sd', '2019-04-27 11:53:02'),
(19, 1, 2, 'dsfdsf', '2019-04-27 11:53:13'),
(20, 1, 2, 'dsfdsf', '2019-04-27 11:54:18'),
(21, 1, 1, 'fdgfg', '2019-04-27 11:54:28'),
(22, 1, 2, 'dsfdsf', '2019-04-27 11:56:55'),
(23, 1, 1, 'dfdsf', '2019-04-27 11:56:59'),
(24, 1, 1, 'hello \n', '2019-04-27 11:57:14'),
(25, 1, 1, 'igor\n', '2019-04-27 11:57:24'),
(26, 1, 2, 'ivan', '2019-04-27 11:57:28'),
(27, 1, 3, 'а я красотка!!!!!', '2019-04-27 19:30:48'),
(28, 1, 3, 'да да', '2019-04-27 19:31:08'),
(29, 1, 3, 'да да да', '2019-04-27 19:33:45'),
(30, 1, 3, 'рщрщр', '2019-04-27 19:35:45'),
(31, 1, 1, 'hello', '2019-04-27 19:36:04'),
(32, 1, 1, 'krasotka moya', '2019-04-27 19:37:06'),
(33, 1, 1, 'lybly tebya:3', '2019-04-27 19:37:28'),
(34, 1, 1, '))))))))))', '2019-04-27 19:37:43'),
(35, 1, 1, 'yes', '2019-04-27 19:37:53'),
(36, 1, 1, 'how are you?\n', '2019-04-27 19:38:02'),
(37, 1, 1, 'littlebig', '2019-04-27 23:20:22'),
(38, 10, 1, 'hello', '2019-04-28 22:36:20'),
(39, 10, 1, 'hellow', '2019-04-28 22:38:36'),
(40, 10, 2, 'how are you&\n', '2019-04-28 22:44:48'),
(41, 10, 2, 'yes', '2019-04-28 22:45:06'),
(42, 23, 1, 'dsfdsfsd', '2019-04-28 23:40:56'),
(43, 23, 1, 'dsfdsf', '2019-04-28 23:40:58'),
(44, 23, 3, 'hi', '2019-04-28 23:44:25'),
(45, 23, 1, 'how are you?', '2019-04-28 23:44:35'),
(46, 10, 3, 'hi', '2019-04-28 23:44:57'),
(47, 23, 1, 'hi', '2019-04-28 23:45:03'),
(48, 15, 1, 'dsfdsfdsf', '2019-04-29 11:10:14'),
(49, 10, 1, 'hello', '2019-04-29 11:27:08'),
(50, 14, 1, 'hello', '2019-05-01 23:47:52');

-- --------------------------------------------------------

--
-- Структура таблицы `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  `login` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `id_organisation` int(11) NOT NULL,
  `manager` int(11) DEFAULT NULL,
  `profession` varchar(255) NOT NULL,
  `avatarURL` varchar(255) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `user`
--

INSERT INTO `user` (`id`, `name`, `surname`, `login`, `password`, `id_organisation`, `manager`, `profession`, `avatarURL`) VALUES
(1, 'Игорь', 'Крейдо', 'witcher', 'ee8d004a628387bcce78653c529e65c6ca3b4714026c82438d851ecc57ba7370', 1, 0, 'Директор компании', 'files\\avatars\\igor.jpg'),
(2, 'Иван', 'Пальмовинов', 'ivan', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 1, 1, 'Front-end developer', 'files\\avatars\\david.jpg'),
(3, 'Венера', 'Труман', 'user1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 1, 1, 'Заместитель директора', 'files\\avatars\\venera.jpg'),
(4, 'Человек2', 'Другой', 'user2', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 1, 1, 'Back-end developer', '0'),
(5, 'Человек 3', 'Другой', 'user3', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 1, 1, 'Подсобный рабочий', '0');

--
-- Триггеры `user`
--
DELIMITER $$
CREATE TRIGGER `trigger_add_auth_user` AFTER INSERT ON `user` FOR EACH ROW BEGIN
  INSERT INTO auth(id_user) VALUES(NEW.id);
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Структура таблицы `user_task`
--

CREATE TABLE `user_task` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `id_task` int(11) NOT NULL,
  `progress` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `user_task`
--

INSERT INTO `user_task` (`id`, `id_user`, `id_task`, `progress`) VALUES
(1, 2, 1, 75),
(7, 2, 9, 100),
(8, 3, 9, 100),
(9, 4, 9, 100),
(10, 1, 10, 0),
(11, 3, 10, 75),
(13, 1, 11, 50),
(14, 3, 11, 100),
(15, 2, 11, 50),
(16, 4, 11, 75),
(17, 1, 12, 100),
(20, 1, 13, 100),
(21, 2, 13, 25),
(22, 1, 14, 0),
(23, 2, 14, 0),
(24, 4, 14, 0),
(25, 1, 15, 100),
(26, 4, 15, 100),
(27, 3, 15, 100),
(28, 1, 16, 100),
(31, 1, 17, 100),
(32, 2, 17, 75),
(33, 4, 17, 100),
(42, 1, 21, 0),
(43, 4, 21, 0),
(44, 3, 21, 0),
(51, 4, 16, 0),
(56, 3, 12, 0),
(58, 5, 12, 0),
(59, 1, 23, 0),
(71, 4, 23, 0),
(72, 3, 23, 50),
(74, 2, 10, 0);

--
-- Триггеры `user_task`
--
DELIMITER $$
CREATE TRIGGER `trigger1` AFTER UPDATE ON `user_task` FOR EACH ROW BEGIN
  DECLARE AVG_PROGRESS INT DEFAULT 0;
    SELECT AVG(user_task.progress) INTO AVG_PROGRESS FROM user_task JOIN task ON user_task.id_task = task.id  WHERE id_task = NEW.id_task AND user_task.id_user != task.id_user  GROUP BY id_task;
    UPDATE task SET task.progress = AVG_PROGRESS WHERE task.id = NEW.id_task;
END
$$
DELIMITER ;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `auth`
--
ALTER TABLE `auth`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_auth_user_id` (`id_user`);

--
-- Индексы таблицы `task`
--
ALTER TABLE `task`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `task_chat`
--
ALTER TABLE `task_chat`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_task_chat_task_id` (`id_task`),
  ADD KEY `FK_task_chat_user_id` (`id_sender`);

--
-- Индексы таблицы `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `user_task`
--
ALTER TABLE `user_task`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_user_task_user_id` (`id_user`),
  ADD KEY `FK_user_task_task_id` (`id_task`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `auth`
--
ALTER TABLE `auth`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT для таблицы `task`
--
ALTER TABLE `task`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;
--
-- AUTO_INCREMENT для таблицы `task_chat`
--
ALTER TABLE `task_chat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;
--
-- AUTO_INCREMENT для таблицы `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT для таблицы `user_task`
--
ALTER TABLE `user_task`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=75;
--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `auth`
--
ALTER TABLE `auth`
  ADD CONSTRAINT `FK_auth_user_id` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `task_chat`
--
ALTER TABLE `task_chat`
  ADD CONSTRAINT `FK_task_chat_user_id` FOREIGN KEY (`id_sender`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `FK_task_chat_task_id` FOREIGN KEY (`id_task`) REFERENCES `task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `user_task`
--
ALTER TABLE `user_task`
  ADD CONSTRAINT `FK_user_task_task_id` FOREIGN KEY (`id_task`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `FK_user_task_user_id` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
