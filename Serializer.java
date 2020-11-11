package ru.mail.polis.homework.io.objects;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Нужно реализовать методы этого класса и реализовать тестирование 4-ех способов записи.
 * Для тестирования надо создать список из 10 разных объектов (заполнять объекты можно рандомом,
 * с помощью класса Random или руками прописать разные значения переменных).
 * Потом получившийся список записать в один и тот же файл 10 раз (100 раз и более, если у вас это происходит очень быстро).
 * Далее этот список надо прочитать из файла.
 * Записывать в существующий файл можно с помощью специального конструктора для файловых потоков
 *
 * Результатом теста должно быть следующее: размер файла, время записи и время чтения.
 * Время считать через System.currentTimeMillis().
 * В итоговом пулРеквесте должна быть информация об этих значениях для каждого теста. (всего 4 теста, за каждый тест 1 балл)
 * Для тестов создайте классы в соотвествующем пакете в папке тестов. Используйте существующие тесты, как примеры.
 *
 * В конце теста по чтению данных, не забывайте удалять файлы
 */
public class Serializer {

    /**
     * 1 балл
     * Реализовать простую сериализацию, с помощью специального потока для сериализации объектов
     * @param animals Список животных для сериализации
     * @param fileName файл в который "пишем" животных
     */
    public void defaultSerialize(List<Animal> animals, String fileName) {
        try(ObjectOutputStream outputStream = new
    ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            outputStream.writeObject(animals);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1 балл
     * Реализовать простую дисериализацию, с помощью специального потока для дисериализации объектов
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<Animal> defaultDeserialize(String fileName){
        List<Animal> result = null;
        try(ObjectInputStream inputStream = new
    ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            result = (List<Animal>)inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 1 балл
     * Реализовать простую ручную сериализацию, с помощью специального потока для сериализации объектов и специальных методов
     * @param animals Список животных для сериализации
     * @param fileName файл в который "пишем" животных
     */
    public void serializeWithMethods(List<AnimalWithMethods> animals, String fileName) {
        try(ObjectOutputStream outputStream = new
    ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            outputStream.writeInt(animals.size());
            for (AnimalWithMethods currentAnimal: animals) {
                outputStream.writeObject(currentAnimal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 1 балл
     * Реализовать простую ручную дисериализацию, с помощью специального потока для дисериализации объектов
     * и специальных методов
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<AnimalWithMethods> deserializeWithMethods(String fileName) {
        List<AnimalWithMethods> result = new ArrayList<AnimalWithMethods>();
        try(ObjectInputStream inputStream = new
    ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            int size = inputStream.readInt();
            for (int i = 0; i < size; i++) {
                result.add((AnimalWithMethods) inputStream.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 1 балл
     * Реализовать простую ручную сериализацию, с помощью специального потока для сериализации объектов и интерфейса Externalizable
     * @param animals Список животных для сериализации
     * @param fileName файл в который "пишем" животных
     */
    public void serializeWithExternalizable(List<AnimalExternalizable> animals, String fileName) {
        try(ObjectOutputStream outputStream = new
    ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            outputStream.writeInt(animals.size());
            for (AnimalExternalizable currentAnimal: animals) {
                outputStream.writeObject(currentAnimal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1 балл
     * Реализовать простую ручную дисериализацию, с помощью специального потока для дисериализации объектов
     * и интерфейса Externalizable
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<AnimalExternalizable> deserializeWithExternalizable(String fileName) {
        List<AnimalExternalizable> result = new ArrayList<AnimalExternalizable>();
        try(ObjectInputStream inputStream = new
    ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            int size = inputStream.readInt();
            for (int i = 0; i < size; i++) {
                result.add((AnimalExternalizable) inputStream.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 2 балла
     * Реализовать ручную сериализацию, с помощью высокоровневых потоков. Сами ручками пишем поля,
     * без использования методов writeObject
     *
     * @param animals  Список животных для сериализации
     * @param fileName файл, в который "пишем" животных
     */
    public void customSerialize(List<Animal> animals, String fileName) {
        try(ObjectOutputStream outputStream = new
    ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            outputStream.writeInt(animals.size());
            for (Animal currentAnimal: animals) {
                outputStream.writeUTF(currentAnimal.getName());
                outputStream.writeBoolean(currentAnimal.isMale());
                outputStream.writeUTF(currentAnimal.getNutritionType().name());
                outputStream.writeInt(currentAnimal.getAge());
                List<String> childNames = currentAnimal.getChildNames();
                outputStream.writeInt(childNames.size());
                for (String currentChild: childNames) {
                    outputStream.writeUTF(currentChild);
                }
                Heart currentHeart = currentAnimal.getHeart();
                outputStream.writeDouble(currentHeart.getWeight());
                outputStream.writeDouble(currentHeart.getPower());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2 балла
     * Реализовать ручную дисериализацию, с помощью высокоуровневых потоков. Сами ручками читаем поля,
     * без использования методов readObject
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<Animal> customDeserialize(String fileName)  {
        List<Animal> result = new ArrayList<Animal>();
        try(ObjectInputStream inputStream = new
    ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            int size = inputStream.readInt();
            for (int i = 0; i < size; i++) {
                String name = inputStream.readUTF();
                boolean isMale = inputStream.readBoolean();
                Animal.NutritionType nutritionType = Animal.NutritionType.
                        valueOf(inputStream.readUTF());
                int age = inputStream.readInt();
                int childNamesSize = inputStream.readInt();
                List childNames = new ArrayList<String>();
                for (int j = 0; j < childNamesSize; j++) {
                    childNames.add(inputStream.readUTF());
                }
                Heart heart = new Heart(inputStream.readDouble(), inputStream.readDouble());
                result.add(new Animal(name, isMale, nutritionType, age, childNames, heart));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}