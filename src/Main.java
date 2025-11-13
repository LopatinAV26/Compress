import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String inputFile = "input.txt";
        String compressedFile = "compressed.txt";
        String restoredFile = "restored.txt";
        
        // Сжатие файла
        compressFile(inputFile, compressedFile);
        
        // Восстановление файла
        decompressFile(compressedFile, restoredFile);
        
        System.out.println("Сжатие и восстановление завершены!");
    }
    
    /**
     * Сжимает файл, удаляя дублирующиеся строки
     * @param inputFile исходный файл
     * @param outputFile сжатый файл
     */
    public static void compressFile(String inputFile, String outputFile) {
        try {
            List<String> lines = new ArrayList<>();
            Map<String, Integer> lineToIndex = new HashMap<>();
            List<Integer> originalLineMapping = new ArrayList<>();
            int duplicatesRemoved = 0;
            
            // Читаем файл
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                // Проверяем, встречалась ли эта строка раньше
                if (lineToIndex.containsKey(line)) {
                    // Это дубликат - сохраняем ссылку на оригинальную строку
                    originalLineMapping.add(lineToIndex.get(line));
                    duplicatesRemoved++;
                } else {
                    // Это уникальная строка
                    int newIndex = lines.size();
                    lines.add(line);
                    lineToIndex.put(line, newIndex);
                    originalLineMapping.add(newIndex);
                }
                lineNumber++;
            }
            reader.close();
            
            // Записываем сжатый файл
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            
            // Первая строка: количество удаленных дубликатов
            writer.println("DUPLICATES_REMOVED: " + duplicatesRemoved);
            
            // Вторая строка: количество уникальных строк
            writer.println("UNIQUE_LINES: " + lines.size());
            
            // Третья строка: общее количество строк в оригинале
            writer.println("TOTAL_LINES: " + lineNumber);
            
            // Разделитель
            writer.println("---UNIQUE_CONTENT---");
            
            // Записываем уникальные строки
            for (String uniqueLine : lines) {
                writer.println(uniqueLine);
            }
            
            // Разделитель
            writer.println("---LINE_MAPPING---");
            
            // Записываем маппинг строк (для восстановления)
            for (int i = 0; i < originalLineMapping.size(); i++) {
                writer.println(i + ":" + originalLineMapping.get(i));
            }
            
            writer.close();
            
            System.out.println("Сжатие завершено. Удалено дублирующихся строк: " + duplicatesRemoved);
            
        } catch (IOException e) {
            System.err.println("Ошибка при сжатии файла: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Восстанавливает полную версию файла из сжатой
     * @param compressedFile сжатый файл
     * @param outputFile восстановленный файл
     */
    public static void decompressFile(String compressedFile, String outputFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(compressedFile));
            
            // Читаем заголовок
            reader.readLine(); // "DUPLICATES_REMOVED: X" - пропускаем
            reader.readLine(); // "UNIQUE_LINES: X" - пропускаем
            String totalLinesLine = reader.readLine();
            reader.readLine(); // "---UNIQUE_CONTENT---"
            
            int totalLines = Integer.parseInt(totalLinesLine.split(": ")[1]);
            
            // Читаем уникальные строки
            List<String> uniqueContent = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null && !line.equals("---LINE_MAPPING---")) {
                uniqueContent.add(line);
            }
            
            // Читаем маппинг строк
            List<Integer> lineMapping = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                int uniqueIndex = Integer.parseInt(parts[1]);
                lineMapping.add(uniqueIndex);
            }
            reader.close();
            
            // Восстанавливаем оригинальный файл
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            for (int i = 0; i < lineMapping.size(); i++) {
                int uniqueIndex = lineMapping.get(i);
                writer.println(uniqueContent.get(uniqueIndex));
            }
            writer.close();
            
            System.out.println("Восстановление завершено. Восстановлено строк: " + totalLines);
            
        } catch (IOException e) {
            System.err.println("Ошибка при восстановлении файла: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
