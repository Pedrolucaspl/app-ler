package com.pedro;

public class App {
    public static void main(String[] args) {
        try {
            Biblioteca biblioteca = new Biblioteca();
            Menu menu = new Menu(biblioteca);
            Menu.limparTela();
            menu.iniciar();
        } catch (Exception e) {
            System.out.println("Erro ao iniciar a aplicação: " + e.getMessage());
        }
    }
}