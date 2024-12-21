package org.example;

import java.io.FileInputStream;
import java.io.IOException;

public class EncryptedClassLoader extends ClassLoader {

    private final String encryptionKey; // Ключ шифрования
    private final String rootDirectory; // Корневая директория для поиска классов

    public EncryptedClassLoader(String encryptionKey, String rootDirectory, ClassLoader parent) {
        super(parent);
        this.encryptionKey = encryptionKey;
        this.rootDirectory = rootDirectory;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // Получаем имя файла класса
        String fileName = name.replace('.', '/') + ".class";
        String fullPath = rootDirectory + "/" + fileName;

        try (FileInputStream inputStream = new FileInputStream(fullPath)) {
            int length = inputStream.available(); // Размер файла
            byte[] encryptedClassData = new byte[length];
            inputStream.read(encryptedClassData); // Читаем зашифрованные данные

            // Простое дешифрирование через XOR
            for (int i = 0; i < length; i++) {
                encryptedClassData[i] ^= encryptionKey.charAt(i % encryptionKey.length()); // XOR с ключом
            }

            // Загружаем класс
            return defineClass(name, encryptedClassData, 0, length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Не удалось найти или прочитать файл класса: " + fullPath, e);
        }
    }

}