

module.exports.values = function(obj) {
  return Object.keys(obj).map(function(name) {
    return obj[name];
  });
};

module.exports.kebapToCamelCase = function(word) {
  return word.trim().replace(/-(\w)/g, function(match, character) {
    return character.toUpperCase();
  });
};

module.exports.capitalize = function(word) {
  return word.trim().substring(0, 1).toUpperCase() + word.substring(1);
};
