### Choose language
* [English](#task-manager-corporative-language-english)
* [Русский](#task-manager-corporative-%D1%8F%D0%B7%D1%8B%D0%BA-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9)

# Task Manager corporative (Язык: русский)
Task manager это удобное решение проблемы планирования. В частности данная поблема отностится к предприятиям,
 так как их успех, развитие и доходность напрямую зависит от количества выполненных задач.
Поэтому данное приложение создано чтобы систематизировать работу компаний.
Составные части:
* Личный кабинет руководителя и сотрудников.
* Распределение задач между сотрудниками
* Управление задачами, просмотр и отслеживание прогресса
* Real-time средство коммуникации (чат по каждой задаче)
* Защищённое соединение SSL

## Цель проекта
Создать мобильное приложение, которое позволит систематизировать работу масштабных предприятий.
<p align="center">
<img src="https://github.com/glorin1/Taskmanager/blob/master/Readme/task_manager_screen1.png"  width="512">
</p>

## Архитектура приложения

В качестве архитектуры приложения была выбрана клиент-серверная схема взаимодействия.
<p align="center">
<img src="https://github.com/itkreydo/TaskmanagerCorporative/blob/master/Readme/task_manager_architecture.png"  width="512">
</p>

## Инструменты реализации
Клиентсткая часть была реализована на мобильной платформе Android.

Серверная часть реализована с помощью программной платформы [Node.js](https://nodejs.org/) в связке с СУБД [MySQL](https://www.mysql.com/) и библиотекой для Real-time обмена данными [Socket.IO](https://socket.io/). Данный стэк технологий позволяет осуществить весь функционал приложения.
<p align="center">
<img src="https://github.com/itkreydo/TaskmanagerCorporative/blob/master/Readme/task_manager_instruments.png"  width="512">
</p>

## Безопасность системы

* Использование двух токенов для авторизации (access, refresh)
* Хеширование паролей алгоритмом SHA-256
* Защита от SQL инъекций
* Защищённое соединение с использованием криптографического протокола SSL

<img src="https://github.com/itkreydo/TaskmanagerCorporative/blob/master/Readme/task_manager_security.png"  align=right width="32">

# Task Manager corporative (Language: english)
Task manager is a comfortable decision of planning problem. 
Success and profit of many companies depends on the ability to correctly manage tasks and planning.
So, this application contains:
* Two level of accounts. Director and employees.
* Distribution of tasks
* Manage task, view and set progress
* Real-time communication chat on the task
* Secure with protocol SSL 

## Objective of the project
Create mobile application that could improve planning and speed up work in corporations.
<p align="center">
<img src="https://github.com/itkreydo/TaskmanagerCorporative/blob/master/Readme/task_manager_screen1.png"  width="512">
</p>

## Application architecture

Application use "client-server" scheme of interaction.
<p align="center">
<img src="https://github.com/itkreydo/TaskmanagerCorporative/blob/master/Readme/task_manager_architecture.png"  width="512">
</p>

## Implementation tools
For realizing as client side was chosen Android mobile platform.

Server side created via programm platform [Node.js](https://nodejs.org/) with database [MySQL](https://www.mysql.com/) and interactive library [Socket.IO](https://socket.io/) which allows to support real-time communication.
<p align="center">
<img src="https://github.com/itkreydo/TaskmanagerCorporative/blob/master/Readme/task_manager_instruments.png"  width="512">
</p>

## System security

* Use two token's for authorisation (access, refresh)
* Hashing password with SHA-256 algorythm.
* SQL injection protection. (by using driver 'mysql' for node.js that escapes invalid characters)
* Security encrypted connection with SSL (Self-Signed certificate in debug version)

<img src="https://github.com/itkreydo/TaskmanagerCorporative/blob/master/Readme/task_manager_security.png"  align=right width="32">



