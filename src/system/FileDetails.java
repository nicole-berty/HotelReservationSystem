package system;

/** Record used to store file details such as the path including file name and the headers for that file
 * Custom immutable type
 * @param path the path to the file including the file name and extension
 * @param headers the headers for the file separated by commas
 */
// 6 - records
public record FileDetails(String path, String headers) {}
