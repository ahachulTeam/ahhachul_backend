CREATE FULLTEXT INDEX ft_index ON tb_lost_post(title, content) WITH PARSER ngram;
