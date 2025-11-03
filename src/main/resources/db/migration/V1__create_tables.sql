CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE livro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    ano INT,
    emprestado BOOLEAN DEFAULT FALSE
);

CREATE TABLE emprestimo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    livro_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_retirada DATE NOT NULL,
    data_prevista_devolucao DATE NOT NULL,
    devolvido BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_livro FOREIGN KEY (livro_id) REFERENCES livro (id),
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);
