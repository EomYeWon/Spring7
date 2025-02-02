--- /*2024 12월 26일 회원전용 게시판*/

-- 1) 회원테이블
use scit;
drop table if exists scit.board_user;

create table scit.board_user
(
	user_id  varchar(50),
	user_pwd varchar(100) not null,
	name 	 varchar(50) not null,  -- 실명 
	email	 varchar(50),
	roles 	 varchar(50) default 'ROLE_USER',
	enabled  char(1)	 default '1',
		constraint boarduser_userid primary key(user_id),
		constraint boarduser_roles check(roles in('ROLE_USER', 'ROLE_ADMIN')),
		constraint boarduser_enabled check(roles in('1','0'))
	
);



-- 2) 게시판 테이블
-- 첨부파ㅣㄹ 관련 컬럼 포함(나중 추가)
drop table if exists scit.reply;
drop table if exists scit.board;

create table scit.board
(
	board_seq 		int auto_increment,
	board_writer 	varchar(50) not null,
	board_title 	varchar(200) default 'untitled',
	board_content 	varchar(4000),
	hit_count		int default 0,			-- 조회수
	create_date		datetime default current_timestamp,
	update_date		datetime default current_timestamp,
		constraint board_boardseq primary key (board_seq)
	
);



-- 3) 댓글 테이블
create table scit.reply
(
	reply_seq		int auto_increment,
	board_seq 		int,
	reply_writer	varchar(50) not null,
	reply_content 	varchar(1000) not null,
	create_date		datetime default current_timestamp,
		constraint	reply_replyseq primary key (reply_seq),
		constraint	reply_boardseq foreign key (board_seq) references scit.board(board_seq)
		on delete cascade
);